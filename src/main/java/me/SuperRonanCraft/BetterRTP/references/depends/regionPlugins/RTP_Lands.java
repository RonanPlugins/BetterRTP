package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import me.angeschossen.lands.api.integration.LandsIntegration;
import org.bukkit.Location;

public class RTP_Lands {

    // NOT TESTED (2.14.3)
    // Lands (v5.0.5)
    // https://www.spigotmc.org/resources/lands.53313/
    public static boolean check(Location loc) {
        boolean result = true;
        if (getDepends().isLands())
            try {
                result = !(new LandsIntegration(BetterRTP.getInstance()).isClaimed(loc));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    private static SoftDepends getDepends() {
        return BetterRTP.getInstance().getSettings().getsDepends();
    }
}
