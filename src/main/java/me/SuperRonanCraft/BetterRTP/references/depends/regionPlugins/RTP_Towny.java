package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.palmergames.bukkit.towny.TownyAPI;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import org.bukkit.Location;

public class RTP_Towny implements RegionPluginCheck {

    // NOT TESTED (2.13.0)
    // Towny (v0.96.1.11)
    // https://www.spigotmc.org/resources/towny.72694/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.TOWNY.isEnabled())
            try {
                result = TownyAPI.getInstance().isWilderness(loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
