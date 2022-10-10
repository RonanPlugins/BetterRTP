package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.bekvon.bukkit.residence.Residence;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.hakan.claimsystem.api.ClaimAPI;
import com.songoda.ultimateclaims.UltimateClaims;
import me.RonanCraft.Pueblos.Pueblos;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
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
