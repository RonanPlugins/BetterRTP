package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import java.lang.reflect.Method;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class RTP_UltimateClaims implements RegionPluginCheck {

    // TESTED (v3.6.12)
    // UltimateClaims (v2.2.0 + v1.10.4)
    // Download @ https://songoda.com/product/ultimateclaims-14
    // V1: https://github.com/craftaro/UltimateClaims/commit/e42376975a59874b05e8516eae1545e94775add5
    // V2: https://github.com/craftaro/UltimateClaims/commit/261d83b8712c5ec967f2aa836521c1ff9065ac91
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
