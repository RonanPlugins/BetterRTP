package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalsCache {

    Player p;
    Location loc_1, loc_2;

    public PortalsCache(Player p) {
        this.p = p;
    }

    void setLoc_1(Location loc) {
        this.loc_1 = loc;
    }

    void setLoc_2(Location loc) {
        this.loc_2 = loc;
    }

    Location getLoc_1() {
        return this.loc_1;
    }

    Location getLoc_2() {
        return this.loc_2;
    }
}
