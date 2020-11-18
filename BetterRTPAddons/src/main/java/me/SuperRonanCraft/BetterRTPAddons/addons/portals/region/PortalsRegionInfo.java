package me.SuperRonanCraft.BetterRTPAddons.addons.portals.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalsRegionInfo {

    protected Location loc_1, loc_2;
    protected String name;

    public Location getLoc1() {
        return loc_1;
    }

    public Location getLoc2() {
        return loc_2;
    }

    public void setLoc1(Location loc) {
        loc_1 = loc;
    }

    public void setLoc2(Location loc) {
        loc_2 = loc;
    }

    public String getName() {
        return name;
    }
}
