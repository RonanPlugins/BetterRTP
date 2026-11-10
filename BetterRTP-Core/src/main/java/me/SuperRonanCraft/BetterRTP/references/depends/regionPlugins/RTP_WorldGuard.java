package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

public class RTP_WorldGuard implements RegionPluginCheck {

    // TESTED (v2.12.3)
    // Worldguard (v7.0.4 B1), WorldEdit (v7.2.0 B5)
    // https://dev.bukkit.org/projects/worldguard
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.WORLDGUARD.isEnabled())
            try {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
                result = set.size() == 0;
            } catch (Exception e) {
               e.printStackTrace();
            }
        return result;
    }
}
