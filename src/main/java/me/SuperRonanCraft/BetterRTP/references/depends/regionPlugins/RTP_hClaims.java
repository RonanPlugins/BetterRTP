package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.hakan.claimsystem.api.ClaimAPI;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import org.bukkit.Location;

public class RTP_hClaims {

    // NOT TESTED (3.1.0)
    // hClaims (v1.1.1)
    // https://www.spigotmc.org/resources/hclaims.90540/ (Local Repo)
    public static boolean check(Location loc) {
        boolean result = true;
        if (getDepends().isHClaims())
            try {
                result = ClaimAPI.getInstance().isClaimed(loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    private static SoftDepends getDepends() {
        return BetterRTP.getInstance().getSettings().getsDepends();
    }
}
