package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import net.william278.huskclaims.BukkitHuskClaims;
import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import org.bukkit.Location;

public class RTP_HuskClaims implements RegionPluginCheck {

    @Override
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.HUSKCLAIMS.isEnabled()) {
            try {
                result = !BukkitHuskClaimsAPI.getInstance().getClaimAt(BukkitHuskClaims.Adapter.adapt(loc)).isPresent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
