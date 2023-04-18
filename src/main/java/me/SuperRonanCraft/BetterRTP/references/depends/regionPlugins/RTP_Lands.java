package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RTP_Lands implements RegionPluginCheck {

    // Implemented (2.14.3)
    // Tested (3.6.2)
    // Lands (v6.28.13)
    // https://www.spigotmc.org/resources/lands.53313/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.LANDS.isEnabled())
            try {
                result = LandsIntegration.of(BetterRTP.getInstance()).getArea(loc) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
