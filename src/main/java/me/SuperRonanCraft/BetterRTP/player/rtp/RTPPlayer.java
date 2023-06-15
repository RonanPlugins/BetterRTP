package me.SuperRonanCraft.BetterRTP.player.rtp;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Check;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.RandomLocation;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RTPPlayer {

    @Getter private final Player player;
    private final RTP settings;
    @Getter WorldPlayer worldPlayer;
    @Getter RTP_TYPE type;
    @Getter int attempts;
    //List<Location> attemptedLocations = new ArrayList<>();

    RTPPlayer(Player player, RTP settings, WorldPlayer worldPlayer, RTP_TYPE type) {
        this.player = player;
        this.settings = settings;
        this.worldPlayer = worldPlayer;
        this.type = type;
    }

    void randomlyTeleport(CommandSender sendi) {
        if (attempts >= settings.maxAttempts) //Cancel out, too many tries
            metMax(sendi, player);
        else { //Try again to find a safe location
            //Find a location from another Plugin
            RTP_FindLocationEvent event = new RTP_FindLocationEvent(this); //Find an external plugin location
            Bukkit.getServer().getPluginManager().callEvent(event);
            //Async Location finder
            if (event.isCancelled()) {
                randomlyTeleport(sendi);
                attempts++;
                return;
            }
            AsyncHandler.async(() -> {
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
                AsyncHandler.sync(() -> {
                    try { //Prior to 1.12 this async chunk will NOT work
                        CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(loc);
                        chunk.thenAccept(result -> {
                            //BetterRTP.debug("Checking location for " + p.getName());
                            attempt(sendi, loc);
                        });
                    } catch (IllegalStateException e) {
                        //Legacy non-async support
                        attempt(sendi, loc);
                    } catch (Throwable ignored) {

                    }
                });
            });
        }
    }

    private void attempt(CommandSender sendi, Location loc) {
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
                AsyncHandler.sync(() -> settings.teleport.sendPlayer(sendi, player, tpLoc, worldPlayer, attempts, type));
            } else {
                if (worldPlayer.getPlayerInfo().applyCooldown)
                    getPl().getCooldowns().removeCooldown(player, worldPlayer.getWorld());
                getPl().getPInfo().getRtping().remove(player);
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
        getPl().getPInfo().getRtping().remove(p);
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