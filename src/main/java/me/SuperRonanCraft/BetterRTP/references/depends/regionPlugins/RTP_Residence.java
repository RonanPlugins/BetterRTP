package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Location;

public class RTP_Residence implements RegionPluginCheck {

    // NOT TESTED (2.14.3)
    // Residence (v4.9.1.9)
    // https://www.spigotmc.org/resources/residence.11480/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.RESIDENCE.isEnabled())
            try {
                // Using reflection
                Class<?> residenceClass = Class.forName("com.bekvon.bukkit.residence.Residence");
                Object residence = residenceClass.getMethod("getInstance").invoke(null);
                Object residenceManager = residenceClass.getMethod("getResidenceManager").invoke(residence);
                Class<?> residenceManagerClass = residenceManager.getClass();
                Object claim = residenceManagerClass.getMethod("getByLoc", Location.class).invoke(residenceManager, loc);
                result = claim == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
