package me.SuperRonanCraft.BetterRTP.player.rtp;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import me.SuperRonanCraft.BetterRTP.Main;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class RTPSoftDepends { //Safe locations depending on enabled dependencies

    boolean checkLocation(Location loc) {
        boolean worldguard = getWorlguard(loc);
        boolean griefPrevention = getGriefprevention(loc);
        return worldguard && griefPrevention;
    }

    private boolean getWorlguard(Location loc) {
        if (getPl().getSettings().getsDepends().isWorldguard())
            try {
                WorldGuardPlugin plugin = WGBukkit.getPlugin();
                RegionContainer container = plugin.getRegionContainer();
                RegionManager regions = container.get(loc.getWorld());
                // Check to make sure that "regions" is not null
                return (regions != null ? regions.getApplicableRegions(loc).size() : 0) == 0;
            } catch (NoClassDefFoundError e) {
                return true;
            }
        return true;
    }

    private boolean getGriefprevention(Location loc) {
        if (getPl().getSettings().getsDepends().isGriefprevention())
            try {
                return GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) == null;
            } catch (NoClassDefFoundError e) {
                return true;
            }
        return true;
    }

    private Main getPl() {
        return Main.getInstance();
    }
}
