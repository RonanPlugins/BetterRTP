package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Location;

import java.lang.reflect.Method;

public class RTP_hClaims implements RegionPluginCheck {

    // NOT TESTED (3.1.0)
    // hClaims (v1.1.1)
    // https://www.spigotmc.org/resources/hclaims.90540/ (Local Repo)
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.HCLAIMS.isEnabled())
            try {
                Class<?> claimHandlerClass = Class.forName("com.hakan.claim.api.ClaimHandler");
                Method hasMethod = claimHandlerClass.getDeclaredMethod("has", Location.class);
                result = (boolean) hasMethod.invoke(null, loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
