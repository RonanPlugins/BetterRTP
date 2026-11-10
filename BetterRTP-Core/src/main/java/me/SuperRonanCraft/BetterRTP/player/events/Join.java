package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.web.Updater;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;

public class Join {

    static void event(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        updater(p);
        AsyncHandler.async(() -> getPl().getCooldowns().loadPlayer(p));
        rtpOnFirstJoin(p);
    }

    //Updater
    private static void updater(Player p) {
        if (!getPl().getFiles().getType(FileOther.FILETYPE.CONFIG).getBoolean("Settings.DisableUpdater") && PermissionNode.UPDATER.check(p)) {
            if (!getPl().getDescription().getVersion().equals(Updater.updatedVersion)) {
                String currentVersion = getPl().getDescription().getVersion();
                String newVersion = Updater.updatedVersion;

                MessagesCore.UPDATE.send(p, Arrays.asList(currentVersion, newVersion));
            }
        }
    }


    //RTP on first join
    private static void rtpOnFirstJoin(Player p) {
        if (getPl().getSettings().isRtpOnFirstJoin_Enabled() && !p.hasPlayedBefore())
            HelperRTP.tp(p, Bukkit.getConsoleSender(), Bukkit.getWorld(getPl().getSettings().getRtpOnFirstJoin_World()), null, RTP_TYPE.JOIN, true, true);
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
