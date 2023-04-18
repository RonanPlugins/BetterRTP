package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import net.william278.husktowns.api.HuskTownsAPI;
import org.bukkit.Location;

public class RTP_HuskTowns implements RegionPluginCheck {

    // NOT TESTED (3.4.5)
    // HuskTowns (v1.8.1)
    // https://www.spigotmc.org/resources/husktowns.92672/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.HUSKTOWNS.isEnabled())
            try {
                result = !HuskTownsAPI.getInstance().getClaimAt(loc).isPresent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
