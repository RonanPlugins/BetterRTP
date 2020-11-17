package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalsCreation {

    public Player p;
    protected Location loc_1, loc_2;

    PortalsCreation(Player p) {
        this.p = p;
    }

    public Location getLoc1() {
        return loc_1;
    }

    public Location getLoc2() {
        return loc_2;
    }
}
