package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import cc.javajobs.factionsbridge.FactionsBridge;
import org.bukkit.Location;

public class RTP_FactionsBridge implements RegionPluginCheck {

    // NOT TESTED (3.6.9)
    // FactionsBridge (v1.3.8)
    // https://www.spigotmc.org/resources/factionsbridge.89716/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.FACTIONSBRIDGE.isEnabled())
            try {
                boolean claimed = FactionsBridge.getFactionsAPI().getClaim(loc).isClaimed();
                result = !claimed;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
