package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.web.Updater;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join {

    static void event(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        updater(p);
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
                getPl().getCooldowns().loadPlayer(p);
            });
        rtpOnFirstJoin(p);
    }

    //Updater
    private static void updater(Player p) {
        if (!getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG).getBoolean("Settings.DisableUpdater") && PermissionNode.UPDATER.check(p))
            if (!getPl().getDescription().getVersion().equals(Updater.updatedVersion))
                getPl().getText().sms(p, "&7There is currently an update for &6BetterRTP &7version &e#" +
                        Updater.updatedVersion + " &7you have version &e#" + getPl().getDescription().getVersion());
    }

    //RTP on first join
    private static void rtpOnFirstJoin(Player p) {
        if (getPl().getSettings().isRtpOnFirstJoin_Enabled() && !p.hasPlayedBefore())
            HelperRTP.tp(p, Bukkit.getConsoleSender(), Bukkit.getWorld(getPl().getSettings().getRtpOnFirstJoin_World()), null, RTP_TYPE.JOIN); //Console is sender to override delays
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
