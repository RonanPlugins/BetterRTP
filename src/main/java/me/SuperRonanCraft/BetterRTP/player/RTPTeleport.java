package me.SuperRonanCraft.BetterRTP.player;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RTPTeleport {

    void sendPlayer(final CommandSender sendi, final Player p, final Location loc, final int price,
                    final int attempts) throws NullPointerException {
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
                    //loc.getWorld().loadChunk(loc.getChunk());
                    p.teleport(loc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getPl().getText().getSoundsEnabled())
                    sounds(p);
            }
        }.runTask(getPl());
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

    private Main getPl() {
        return Main.getInstance();
    }
}
