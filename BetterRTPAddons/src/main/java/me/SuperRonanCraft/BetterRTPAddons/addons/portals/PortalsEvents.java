package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PortalsEvents implements Listener {

    AddonPortals addonPortals;

    PortalsEvents(AddonPortals addonPortals) {
        this.addonPortals = addonPortals;
    }

    public void register() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void unregiter() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler (priority = EventPriority.MONITOR)
    void move(PlayerMoveEvent e) {
        if (e.getFrom() != e.getTo()) {
            for (PortalsRegionInfo portal : addonPortals.getPortals().getRegisteredPortals()) {
                Location loc1 = portal.getLoc1(), loc2 = portal.getLoc2();
                for (int x = Math.max(loc1.getBlockX(), loc2.getBlockX()); x >= Math.min(loc1.getBlockX(), loc2.getBlockX()); x--)
                    for (int y = Math.max(loc1.getBlockY(), loc2.getBlockY()); y >= Math.min(loc1.getBlockY(), loc2.getBlockY()); y--)
                        for (int z = Math.max(loc1.getBlockZ(), loc2.getBlockZ()); z >= Math.min(loc1.getBlockZ(), loc2.getBlockZ()); z--) {
                            e.getPlayer().sendMessage("Inside Region!");
                        }
            }
        }
    }
}
