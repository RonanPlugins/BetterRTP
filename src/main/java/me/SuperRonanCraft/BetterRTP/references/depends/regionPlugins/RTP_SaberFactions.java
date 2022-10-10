package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import com.massivecraft.factions.*;
import me.RonanCraft.Pueblos.Pueblos;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import org.bukkit.Location;

public class RTP_SaberFactions implements RegionPluginCheck {

    // NOT TESTED (3.4.5)
    // SaberFactions (v2.0.1)
    // https://www.spigotmc.org/resources/saberfactions.69771/
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.SABERFACTIONS.isEnabled())
            try {
                FLocation fLoc = new FLocation(loc);
                Faction faction = Board.getInstance().getFactionAt(fLoc);
                result = faction == null || faction.isWilderness() || faction.isSafeZone();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
