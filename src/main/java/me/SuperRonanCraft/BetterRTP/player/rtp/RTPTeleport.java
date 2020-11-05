package me.SuperRonanCraft.BetterRTP.player.rtp;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//---
//Credit to @PaperMC for PaperLib - https://github.com/PaperMC/PaperLib
//
//Use of asyncronous chunk loading and teleporting
//---

public class RTPTeleport {

    private final RTPParticles eParticles = new RTPParticles();
    private final RTPPotions ePotions = new RTPPotions();
    private final RTPSounds eSounds = new RTPSounds();
    private final RTPTitles eTitles = new RTPTitles();

    //public HashMap<Player, List<CompletableFuture<Chunk>>> playerLoads = new HashMap<>();

    void load() {
        eParticles.load();
        ePotions.load();
        eSounds.load();
        eTitles.load();
    }

//    void cancel(Player p) { //Cancel loading chunks/teleporting
//        if (!playerLoads.containsKey(p)) return;
//        List<CompletableFuture<Chunk>> asyncChunks = playerLoads.get(p);
//        CompletableFuture.allOf(asyncChunks.toArray(new CompletableFuture[] {})).cancel(true);
//    }

    void sendPlayer(final CommandSender sendi, final Player p, final Location loc, final int price,
                    final int attempts) throws NullPointerException {
        loadingTeleport(p, sendi); //Send loading message to player who requested
        List<CompletableFuture<Chunk>> asyncChunks = getChunks(loc); //Get a list of chunks
        //playerLoads.put(p, asyncChunks);
        CompletableFuture.allOf(asyncChunks.toArray(new CompletableFuture[] {})).thenRun(() -> { //Async chunk load
            new BukkitRunnable() { //Run synchronously
                @Override
                public void run() {
                    try {
                        PaperLib.teleportAsync(p, loc).thenRun(new BukkitRunnable() { //Async teleport
                            @Override
                            public void run() {
                                afterTeleport(p, loc, price, attempts);
                                if (sendi != p) //Tell player who requested that the player rtp'd
                                    sendSuccessMsg(sendi, p.getName(), loc, price, false, attempts);
                                getPl().getCmd().rtping.remove(p.getUniqueId()); //No longer rtp'ing
                            }
                        });
                    } catch (Exception e) {
                        getPl().getCmd().rtping.remove(p.getUniqueId()); //No longer rtp'ing (errored)
                        e.printStackTrace();
                    }
                }
            }.runTask(getPl());
        });
    }

    public void afterTeleport(Player p, Location loc, int price, int attempts) { //Only a successful rtp should run this OR '/rtp test'
        eSounds.playTeleport(p);
        eParticles.display(p);
        ePotions.giveEffects(p);
        eTitles.showTitle(RTPTitles.RTP_TITLE_TYPE.TELEPORT, p, loc, attempts, 0);
        if (eTitles.sendMsg(RTPTitles.RTP_TITLE_TYPE.TELEPORT))
            sendSuccessMsg(p, p.getName(), loc, price, true, attempts);
    }

    public void beforeTeleportInstant(CommandSender sendi, Player p) {
        eSounds.playDelay(p);
        eTitles.showTitle(RTPTitles.RTP_TITLE_TYPE.NODELAY, p, p.getLocation(), 0, 0);
        if (eTitles.sendMsg(RTPTitles.RTP_TITLE_TYPE.NODELAY))
            getPl().getText().getSuccessTeleport(sendi);
    }

    public void beforeTeleportDelay(Player p, int delay) { //Only Delays should call this
        eSounds.playDelay(p);
        eTitles.showTitle(RTPTitles.RTP_TITLE_TYPE.DELAY, p, p.getLocation(), 0, delay);
        if (eTitles.sendMsg(RTPTitles.RTP_TITLE_TYPE.DELAY))
            getPl().getText().getDelay(p, delay);
    }

    public void cancelledTeleport(Player p) { //Only Delays should call this
        eTitles.showTitle(RTPTitles.RTP_TITLE_TYPE.CANCEL, p, p.getLocation(), 0, 0);
        if (eTitles.sendMsg(RTPTitles.RTP_TITLE_TYPE.CANCEL))
            getPl().getText().getMoved(p);
    }

    private void loadingTeleport(Player p, CommandSender sendi) {
        eTitles.showTitle(RTPTitles.RTP_TITLE_TYPE.LOADING, p, p.getLocation(), 0, 0);
        if (eTitles.sendMsg(RTPTitles.RTP_TITLE_TYPE.LOADING) || sendi != p) //Show msg if enabled or if not same player
            getPl().getText().getSuccessLoading(sendi);
    }

    private List<CompletableFuture<Chunk>> getChunks(Location loc) { //List all chunks in range to load
        List<CompletableFuture<Chunk>> asyncChunks = new ArrayList<>();
        int range = Math.round(Math.max(0, Math.min(16, getPl().getSettings().preloadRadius)));
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                Location locLoad = new Location(loc.getWorld(), loc.getX() + (x * 16), loc.getY(), loc.getZ() + (z * 16));
                CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(locLoad, true);
                asyncChunks.add(chunk);
            }
        }
        return asyncChunks;
    }

    private void sendSuccessMsg(CommandSender sendi, String player, Location loc, int price, boolean sameAsPlayer,
                                int attempts) {
        String x = Integer.toString(loc.getBlockX());
        String y = Integer.toString(loc.getBlockY());
        String z = Integer.toString(loc.getBlockZ());
        String world = loc.getWorld().getName();
        if (sameAsPlayer) {
            if (price == 0)
                getPl().getText().getSuccessBypass(sendi, x, y, z, world, attempts);
            else
                getPl().getText().getSuccessPaid(sendi, price, x, y, z, world, attempts);
        } else
            getPl().getText().getOtherSuccess(sendi, player, x, y, z, world, attempts);
    }

    private Main getPl() {
        return Main.getInstance();
    }

    public void failed(Player p) {
        eTitles.showTitle(RTPTitles.RTP_TITLE_TYPE.FAILED, p, p.getLocation(), 0, 0);
    }
}
