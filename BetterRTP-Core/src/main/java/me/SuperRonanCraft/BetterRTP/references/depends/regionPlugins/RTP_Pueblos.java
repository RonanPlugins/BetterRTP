package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Location;

import me.RonanCraft.Pueblos.Pueblos;

public class RTP_Pueblos implements RegionPluginCheck {

    // NOT TESTED (3.1.0)
    // Pueblos (v2.0.1)
    // https://www.spigotmc.org/resources/pueblos.91255/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.PUEBLOS.isEnabled())
            try {
                result = Pueblos.getInstance().getClaimHandler().getClaimMain(loc) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
