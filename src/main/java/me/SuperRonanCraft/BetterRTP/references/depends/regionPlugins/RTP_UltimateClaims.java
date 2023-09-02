package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.lang.reflect.Method;

public class RTP_UltimateClaims implements RegionPluginCheck {

    // NOT TESTED (3.1.0)
    // UltimateClaims (v1.6.1) - Abandoned
    // https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.ULTIMATECLAIMS.isEnabled())
            try {
                Chunk chunk = loc.getChunk();

                // Get instance of UltimateClaims
                Class<?> ultimateClaimsClass;
                try {
                    ultimateClaimsClass = Class.forName("com.songoda.ultimateclaims.UltimateClaims");
                } catch(ClassNotFoundException error) {
                    ultimateClaimsClass = Class.forName("com.craftaro.ultimateclaims.UltimateClaims");
                }
                Method getInstanceMethod = ultimateClaimsClass.getMethod("getInstance");
                Object ultimateClaims = getInstanceMethod.invoke(null);

                // Get the ClaimManager
                Method getClaimManagerMethod = ultimateClaimsClass.getMethod("getClaimManager");
                Object claimManager = getClaimManagerMethod.invoke(ultimateClaims);

                // Get the claim based on the chunk
                Method getClaimMethod = claimManager.getClass().getMethod("getClaim", Chunk.class);
                Object claimObj = getClaimMethod.invoke(claimManager, chunk);

                // Check if a claim exists
                return claimObj == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
