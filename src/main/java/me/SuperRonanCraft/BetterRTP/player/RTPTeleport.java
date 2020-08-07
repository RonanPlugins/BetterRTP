package me.SuperRonanCraft.BetterRTP.player;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RTPTeleport {

    void sendPlayer(final CommandSender sendi, final Player p, final Location loc, final int price,
                    final int attempts) throws NullPointerException {
        getPl().getText().getSuccessLoading(sendi); //Send loading message
        loadChunks(loc); //Load chunks before teleporting
        new BukkitRunnable(){
            @Override
            public void run() {
                if (sendi != p)
                    checkPH(sendi, p.getDisplayName(), loc, price, false, attempts);
                if (getPl().getText().getTitleSuccessChat())
                    checkPH(p, p.getDisplayName(), loc, price, true, attempts);
                if (getPl().getText().getTitleEnabled())
                    titles(p, loc, attempts);
                try {
                    //p.teleport(loc);
                    PaperLib.teleportAsync(p, loc).thenRun(new BukkitRunnable() { //Async teleport
                        @Override
                        public void run() {
                            if (getPl().getText().getSoundsEnabled())
                                sounds(p);
                            particles(p);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getPl().getCmd().rtping.put(p.getUniqueId(), false); //Dont let them rtp again until current is done!
                }
        }.runTask(getPl());
    }

    private void loadChunks(Location loc) { //Async chunk loading
        List<CompletableFuture<Chunk>> asyncChunks = new ArrayList<>();
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Location locLoad = new Location(loc.getWorld(), loc.getX() + (x * 16), loc.getY(), loc.getZ() + (x * 16));
                CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(locLoad, true);
                asyncChunks.add(chunk);
            }
        }
        while (!checkLoaded(asyncChunks)) {
            try {
                Thread.sleep(500); //Sleep and check again 0.5 seconds later
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkLoaded(List<CompletableFuture<Chunk>> asyncChunks) {
        for (CompletableFuture<Chunk> chunk : asyncChunks)
            if (!chunk.isDone())
                return false;
        return true;
    }

    private void checkPH(CommandSender sendi, String player, Location loc, int price, boolean sameAsPlayer,
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

    @SuppressWarnings({"deprecation"})
    private void titles(Player p, Location loc, int attempts) {
        // int fadeIn = getPl().text.getFadeIn();
        // int stay = text.getStay();
        // int fadeOut = text.getFadeOut();
        String x = String.valueOf(loc.getBlockX());
        String y = String.valueOf(loc.getBlockY());
        String z = String.valueOf(loc.getBlockZ());
        String title = getPl().getText().getTitleSuccess(p.getName(), x, y, z, attempts);
        String subTitle = getPl().getText().getSubTitleSuccess(p.getName(), x, y, z, attempts);
        // player.sendMessage(Bukkit.getServer().getVersion());
        // player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        p.sendTitle(title, subTitle);
    }

    private void sounds(Player p) {
        Sound sound = getPl().getText().getSoundsSuccess();
        if (sound != null)
            p.playSound(p.getLocation(), sound, 1F, 1F);
    }

    private void particles(Player p) {
        if (getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG).getBoolean("Settings.Particles.Enabled"))
            try {
                String type = getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG).getString("Settings.Particles.Type");
                ParticleEffect effect = ParticleEffect.valueOf(type.toUpperCase());
                int radius = 30;
                int precision = getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG).getInt("Settings.Particles.Amount");;
                Location loc = p.getLocation().add(new Vector(0, 2, 0));
                for (int i = 1; i < precision; i++) {
                    double p1 = (i * Math.PI) / (precision / 2);
                    double p2 = (i - 1) * Math.PI / (precision / 2);

                    double x1 = Math.cos(p1) * radius;
                    double x2 = Math.cos(p2) * radius;
                    double z1 = Math.sin(p1) * radius;
                    double z2 = Math.sin(p2) * radius;
                    Vector vec = new Vector(x2 - x1, 0, z2 - z1);
                    effect.display(loc.clone().add(vec), new Vector(0, -0.125, 0), 1f, 0, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private Main getPl() {
        return Main.getInstance();
    }
}
