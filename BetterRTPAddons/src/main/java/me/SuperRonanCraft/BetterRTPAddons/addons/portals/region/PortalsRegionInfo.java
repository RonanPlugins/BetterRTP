package me.SuperRonanCraft.BetterRTPAddons.addons.portals.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PortalsRegionInfo {

    protected Location loc_1, loc_2;
    protected String name;
    protected String world;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setWorld(@Nullable String world) {
        this.world = world;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getWorld() {
        return world;
    }
}
