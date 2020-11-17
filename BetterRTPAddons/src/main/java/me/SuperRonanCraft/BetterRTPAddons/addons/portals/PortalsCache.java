package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PortalsCache {

    private final HashMap<Player, PortalsCreation> portalsBeingCreated = new HashMap<>();

    void unload() {
        portalsBeingCreated.clear();
    }

    void uncache(Player p) {
        portalsBeingCreated.remove(p);
    }

    public void setPortal(Player p, Location loc, boolean loc2) {
        PortalsCreation portal;
        if (portalsBeingCreated.containsKey(p)) {
            portal = portalsBeingCreated.get(p);
        } else
            portal = new PortalsCreation(p);
        if (loc2) portal.loc_2 = loc;
        else portal.loc_1 = loc;
    }

    public PortalsCreation getPortal(Player p) {
        return portalsBeingCreated.getOrDefault(p, null);
    }
}
