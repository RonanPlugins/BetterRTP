package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.songoda.ultimateclaims.UltimateClaims;
import me.RonanCraft.Pueblos.Pueblos;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import org.bukkit.Location;

public class RTP_GriefDefender implements RegionPluginCheck {

    // NOT TESTED (3.1.0)
    // GriefDefender (v1.5.10)
    // https://www.spigotmc.org/resources/griefdefender.68900/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.GRIEFDEFENDER.isEnabled())
            try {
                for (Claim claim : GriefDefender.getCore().getAllClaims())
                    if (claim.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                        result = false;
                        break;
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
