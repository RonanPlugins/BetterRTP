package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class RTP_GriefPrevention {

    // TESTED (v2.13.0)
    // GriefPrevention (v16.15.0)
    // https://www.spigotmc.org/resources/griefprevention.1884/
    public static boolean check(Location loc) {
        boolean result = true;
        if (getDepends().isGriefprevention())
            try {
                result = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    private static SoftDepends getDepends() {
        return BetterRTP.getInstance().getSettings().getsDepends();
    }
}
