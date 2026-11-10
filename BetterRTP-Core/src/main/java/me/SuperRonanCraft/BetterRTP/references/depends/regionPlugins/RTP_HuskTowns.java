package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import net.william278.husktowns.api.BukkitHuskTownsAPI;
import org.bukkit.Location;

public class RTP_HuskTowns implements RegionPluginCheck {

    @Override
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.HUSKTOWNS.isEnabled()) {
            try {
                result = !BukkitHuskTownsAPI.getInstance().getClaimAt(loc).isPresent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}