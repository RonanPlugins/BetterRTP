package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.songoda.ultimateclaims.UltimateClaims;
import me.RonanCraft.Pueblos.Pueblos;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import org.bukkit.Location;

public class RTP_UltimateClaims {

    // NOT TESTED (3.1.0)
    // UltimateClaims (v1.6.1)
    // https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65
    public static boolean check(Location loc) {
        boolean result = true;
        if (getDepends().isUltimateClaims())
            try {
                result = UltimateClaims.getInstance().getClaimManager().getClaim(loc.getChunk()) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    private static SoftDepends getDepends() {
        return BetterRTP.getInstance().getSettings().getsDepends();
    }
}
