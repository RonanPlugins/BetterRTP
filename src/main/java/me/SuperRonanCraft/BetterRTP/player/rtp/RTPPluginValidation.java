package me.SuperRonanCraft.BetterRTP.player.rtp;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.palmergames.bukkit.towny.TownyAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.SuperRonanCraft.BetterRTP.Main;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class RTPPluginValidation { //Safe locations depending on enabled dependencies

    boolean checkLocation(Location loc) {
        boolean worldguard = getWorlguard(loc);
        boolean griefPrevention = getGriefprevention(loc);
        boolean towny = getTowny(loc);
        boolean redProtect = getRedProtect(loc);
        boolean factionsUUID = getFactionsUUID(loc);
        return worldguard && griefPrevention && towny && redProtect && factionsUUID;
    }

    // TESTED on v2.12.3
    // Worldguard v7.0.4 B1, WorldEdit v7.2.0 B5
    // https://dev.bukkit.org/projects/worldguard
    private boolean getWorlguard(Location loc) {
        boolean result = true;
        if (getPl().getSettings().getsDepends().isWorldguard())
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

    // TESTED on v2.13.0
    // GriefPrevention v16.15.0
    // https://www.spigotmc.org/resources/griefprevention.1884/
    private boolean getGriefprevention(Location loc) {
        boolean result = true;
        if (getPl().getSettings().getsDepends().isGriefprevention())
            try {
                result = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED
    // Towny v0.96.1.11
    // https://www.spigotmc.org/resources/towny.72694/
    private boolean getTowny(Location loc) {
        boolean result = true;
        if (getPl().getSettings().getsDepends().isTowny())
            try {
                result = TownyAPI.getInstance().isWilderness(loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // TESTED 2.13.0
    // RedProtect v7.7.2
    // https://www.spigotmc.org/resources/redprotect.15841/
    private boolean getRedProtect(Location loc) {
        boolean result = true;
        if (getPl().getSettings().getsDepends().isRedProtect())
            try {
                result = RedProtect.get().getAPI().getRegion(loc) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED 2.13.2
    // FactionsUUID v1.6.9.5-U0.5.16
    // https://www.spigotmc.org/resources/factionsuuid.1035/
    private boolean getFactionsUUID(Location loc) {
        boolean result = true;
        if (getPl().getSettings().getsDepends().isFactionsUUID())
            try {
                Faction faction = Board.getInstance().getFactionAt(new FLocation(loc));
                result = faction.isWilderness() || faction.isWarZone() || faction.isSafeZone();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED 2.14.2
    // Residence v2.5.8
    // https://www.spigotmc.org/resources/residence.11480/
    /*private boolean getResidence(Location loc) {
        boolean result = true;
        if (getPl().getSettings().getsDepends().isFactionsUUID())
            try {
                ResidenceApi resAPI = Residence.getAPI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }*/

    private Main getPl() {
        return Main.getInstance();
    }
}
