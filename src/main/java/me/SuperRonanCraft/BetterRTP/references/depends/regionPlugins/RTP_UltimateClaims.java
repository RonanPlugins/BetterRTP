package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class RTP_UltimateClaims implements RegionPluginCheck {

    // TESTED (v2.2.0)
    // UltimateClaims (v2.2.0 + v1.10.4)
    // https://craftaro.com/marketplace/product/65
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.ULTIMATECLAIMS.isEnabled())
            try {
                JavaPlugin ultimateClaimsInstance = JavaPlugin.getPlugin((Class<? extends JavaPlugin>) getPluginMainClass());

                // Get the ClaimManager
                Method getClaimManagerMethod = ultimateClaimsInstance.getClass().getMethod("getClaimManager");
                Object claimManager = getClaimManagerMethod.invoke(ultimateClaimsInstance);

                // Get the claim based on the chunk
                Method hasClaimMethod = claimManager.getClass().getMethod("hasClaim", Chunk.class);
                return Boolean.FALSE.equals(hasClaimMethod.invoke(claimManager, loc.getChunk()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    private Class<?> getPluginMainClass() throws ClassNotFoundException {
        try {
            return Class.forName("com.craftaro.ultimateclaims.UltimateClaims"); // v2
        } catch (ClassNotFoundException ignore) {
            return Class.forName("com.songoda.ultimateclaims.UltimateClaims");  // v1
        }
    }
}
