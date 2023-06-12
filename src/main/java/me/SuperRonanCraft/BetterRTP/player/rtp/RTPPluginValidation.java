package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins.REGIONPLUGINS;
import org.bukkit.Location;

public class RTPPluginValidation {

    /**
     * @param loc Location to check
     * @return True if valid location
     */
    public static boolean checkLocation(Location loc) {
        for (REGIONPLUGINS validators : REGIONPLUGINS.values())
            if (!validators.getValidator().check(loc))
                return false;
        return true;
    }
}
