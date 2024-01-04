package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Location;

public class RTP_KingdomsX implements RegionPluginCheck {

    // NOT TESTED (3.6.12)
    // KingdomsX (v1.16.8.1.1)
    // https://www.spigotmc.org/resources/kingdomsx.77670/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.KINGDOMSX.isEnabled())
            try {
                org.kingdoms.constants.land.Land land = org.kingdoms.constants.land.Land.getLand(loc);
                result = land == null || !land.isClaimed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
