package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.Updater;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join {

    void event(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        updater(p);
        rtpOnFirstJoin(p);
    }

    //Updater
    private void updater(Player p) {
        if (!getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG).getBoolean("Settings.DisableUpdater") && getPl().getPerms().getUpdate(p))
            if (!getPl().getDescription().getVersion().equals(Updater.updatedVersion))
                getPl().getText().sms(p, "&7There is currently an update for &6BetterRTP &7version &e#" +
                        Updater.updatedVersion + " &7you have version &e#" + getPl().getDescription().getVersion());
    }

    //RTP on first join
    private void rtpOnFirstJoin(Player p) {
        if (getPl().getSettings().firstJoinRtp && !p.hasPlayedBefore()) {

        }
    }

    private Main getPl() {
        return Main.getInstance();
    }
}
