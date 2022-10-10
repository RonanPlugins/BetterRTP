package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins.REGIONPLUGINS;
import org.bukkit.Location;

public class RTPPluginValidation {

    public static boolean checkLocation(Location loc) {
        for (REGIONPLUGINS validators : REGIONPLUGINS.values())
            if (!validators.getValidator().check(loc))
                return false;
        return true;
    }
}
