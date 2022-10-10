package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import me.angeschossen.lands.api.integration.LandsIntegration;
import org.bukkit.Location;

public class RTP_Lands implements RegionPluginCheck {

    // NOT TESTED (2.14.3)
    // Lands (v5.0.5)
    // https://www.spigotmc.org/resources/lands.53313/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.LANDS.isEnabled())
            try {
                result = !(new LandsIntegration(BetterRTP.getInstance()).isClaimed(loc));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
