package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.Updater;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join {

    void event(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Main pl = Main.getInstance();
        if (!pl.getFiles().getType(FileBasics.FILETYPE.CONFIG).getBoolean("Settings.DisableUpdater") && pl.getPerms().getUpdate(player))
            if (!pl.getDescription().getVersion().equals(Updater.updatedVersion))
                pl.getText().sms(player, "&7There is currently an update for &6BetterRTP &7version &e#" +
                        Updater.updatedVersion + " &7you have version &e#" + pl.getDescription().getVersion());
    }
}
