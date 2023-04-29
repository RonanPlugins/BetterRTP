package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.bekvon.bukkit.residence.Residence;
import org.bukkit.Location;

public class RTP_Residence implements RegionPluginCheck {

    // NOT TESTED (2.14.3)
    // Residence (v4.9.1.9)
    // https://www.spigotmc.org/resources/residence.11480/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.RESIDENCE.isEnabled())
            try {
                result = Residence.getInstance().getResidenceManager().getByLoc(loc) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
