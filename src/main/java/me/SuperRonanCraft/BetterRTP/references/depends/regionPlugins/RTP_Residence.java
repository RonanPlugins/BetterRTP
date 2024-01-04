package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Location;

public class RTP_Residence implements RegionPluginCheck {

    // NOT TESTED (3.6.12)
    // Residence (v5.1.4.1)
    // https://www.spigotmc.org/resources/residence.11480/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.RESIDENCE.isEnabled())
            try {
                // Don't import to avoid class loader issues
                com.bekvon.bukkit.residence.Residence instance = com.bekvon.bukkit.residence.Residence.getInstance();
                com.bekvon.bukkit.residence.protection.ClaimedResidence claim = instance.getResidenceManagerAPI().getByLoc(loc);
                result = claim == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
