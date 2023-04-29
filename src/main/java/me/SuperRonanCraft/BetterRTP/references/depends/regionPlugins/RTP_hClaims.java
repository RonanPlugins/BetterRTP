package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.hakan.claim.api.ClaimHandler;
import org.bukkit.Location;

public class RTP_hClaims implements RegionPluginCheck {

    // NOT TESTED (3.1.0)
    // hClaims (v1.1.1)
    // https://www.spigotmc.org/resources/hclaims.90540/ (Local Repo)
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.HCLAIMS.isEnabled())
            try {
                result = ClaimHandler.has(loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
