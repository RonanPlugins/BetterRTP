package me.SuperRonanCraft.BetterRTPAddons.util;

import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdReload;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CommandEvent;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReloadListener implements Listener {

    public ReloadListener(Main pl) {
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    void reload(RTP_CommandEvent e) {
        if (e.getCmd() instanceof CmdReload)
            Main.getInstance().load();
    }
}
