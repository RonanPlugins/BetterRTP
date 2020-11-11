package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Events implements Listener {

    void load() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    void onTeleport(RTP_TeleportEvent e) {
        System.out.println("Player " + e.getPlayer().getName() + " was rtp'd!");
        new PlayerFlashback(e.getPlayer(), e.getOldLocation(), 20L * 10);
    }

}
