package me.SuperRonanCraft.BetterRTP.player.rtp;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FailedEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Check;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.RandomLocation;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;

public class RTPPlayer {

    @Getter private final Player player;
    @Getter private final RTPOperationTracker.Operation operation;
    private final RTP settings;
    @Getter WorldPlayer worldPlayer;
    @Getter RTP_TYPE type;
    @Getter int attempts;
    //List<Location> attemptedLocations = new ArrayList<>();

    RTPPlayer(Player player, RTP settings, WorldPlayer worldPlayer, RTP_TYPE type, RTPOperationTracker.Operation operation) {
        this.operation = operation;
        this.player = player;
        this.settings = settings;
        this.worldPlayer = worldPlayer;
        this.type = type;
    }

    boolean isActive() {
        if (!player.isOnline()) operation.finish();
        return operation.isActive();
    }

    void logError(Throwable error) {
        getPl().getLogger().log(java.util.logging.Level.WARNING, "RTP operation failed for " + player.getUniqueId(), error);
    }

    void runSafely(Runnable action) {
        try {
            if (isActive()) action.run();
        } catch (Throwable error) {
            try { operation.finish(); } finally { logError(error); }
        }
    }

    void afterPreEvent(boolean cancelled, Runnable continuation) {
        if (cancelled) operation.finish();
        else runSafely(continuation);
    }

    void awaitChunk(CompletableFuture<Chunk> chunk, Runnable continuation) {
        chunk.whenComplete((result, error) -> runSafely(() -> {
            if (error != null) {
                try { operation.finish(); } finally { logError(error); }
            } else continuation.run();
        }));
    }

    void randomlyTeleport(CommandSender sendi) {
        runSafely(() -> search(sendi));
    }

    private void search(CommandSender sendi) {
        if (attempts >= settings.maxAttempts) //Cancel out, too many tries
            metMax(sendi, player);
        else { //Try again to find a safe location
            //Find a location from another Plugin
            RTP_FindLocationEvent event = new RTP_FindLocationEvent(this); //Find an external plugin location
            Bukkit.getServer().getPluginManager().callEvent(event);
            //Async Location finder
            if (event.isCancelled()) {
                attempts++;
                AsyncHandler.sync(() -> randomlyTeleport(sendi));
                return;
            }
            AsyncHandler.async(() -> runSafely(() -> {
                Location loc;
                if (event.getLocation() != null) // && WorldPlayer.checkIsValid(event.getLocation(), pWorld))
                    loc = event.getLocation();
                else {
                    QueueData queueData = QueueHandler.getRandomAsync(worldPlayer);
                    //BetterRTP.getInstance().getLogger().warning("Center x " + worldPlayer.getCenterX());
                    if (queueData != null)
                        loc = queueData.getLocation();
                    else
                        loc = RandomLocation.generateLocation(worldPlayer);
                }
                attempts++; //Add an attempt
                //Load chunk and find out if safe location (asynchronously)
                AsyncHandler.sync(() -> runSafely(() -> {
                    try { //Prior to 1.12 this async chunk will NOT work
                        CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(loc);
                        awaitChunk(chunk, () -> attempt(sendi, loc));
                    } catch (IllegalStateException e) {
                        //Legacy non-async support
                        attempt(sendi, loc);
                    }
                }));
            }));
        }
    }

    private void attempt(CommandSender sendi, Location loc) {
        if (!isActive()) return;
        Location tpLoc;
        tpLoc = RandomLocation.getSafeLocation(worldPlayer.getWorldtype(), worldPlayer.getWorld(), loc, worldPlayer.getMinY(), worldPlayer.getMaxY(), worldPlayer.getBiomes());
        //attemptedLocations.add(loc);
        //Valid location?
        if (tpLoc != null && checkDepends(tpLoc)) {
            tpLoc.add(0.5, 0, 0.5); //Center location
            if (getPl().getEco().charge(player, worldPlayer)) {
                //Successfully found a safe location, set cooldown and teleport player.
                if (worldPlayer.getPlayerInfo().isApplyCooldown() && HelperRTP_Check.applyCooldown(player))
                    getPl().getCooldowns().add(player, worldPlayer.getWorld());
                tpLoc.setYaw(player.getLocation().getYaw());
                tpLoc.setPitch(player.getLocation().getPitch());
                AsyncHandler.sync(() -> runSafely(() -> settings.teleport.sendPlayer(sendi, this, tpLoc)));
            } else {
                if (worldPlayer.getPlayerInfo().applyCooldown)
                    getPl().getCooldowns().removeCooldown(player, worldPlayer.getWorld());
                operation.finish();
            }
        } else {
            randomlyTeleport(sendi);
            QueueHandler.remove(loc);
        }
    }

    // Compressed code for MaxAttempts being met
    private void metMax(CommandSender sendi, Player p) {
        settings.teleport.failedTeleport(p, sendi);
        getPl().getCooldowns().removeCooldown(p, worldPlayer.getWorld());
        operation.finish();
        //RTP Failed Event
        Bukkit.getServer().getPluginManager().callEvent(new RTP_FailedEvent(this));
    }

    /**
     * @param loc Location to check
     * @return True if the location is valid
     */
    public static boolean checkDepends(Location loc) {
        return RTPPluginValidation.checkLocation(loc);
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}