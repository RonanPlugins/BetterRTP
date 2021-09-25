package me.SuperRonanCraft.BetterRTP.player.rtp;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import com.bekvon.bukkit.residence.Residence;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.hakan.claimsystem.api.ClaimAPI;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.palmergames.bukkit.towny.TownyAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.songoda.ultimateclaims.UltimateClaims;
import me.RonanCraft.Pueblos.Pueblos;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class RTPPluginValidation { //Safe locations depending on enabled dependencies

    boolean checkLocation(Location loc) {
        boolean plugin_worldguard = getWorlguard(loc);
        boolean plugin_griefPrevention = getGriefprevention(loc);
        boolean plugin_towny = getTowny(loc);
        boolean plugin_redProtect = getRedProtect(loc);
        boolean plugin_factionsUUID = getFactionsUUID(loc);
        boolean plugin_lands = getLands(loc);
        boolean plugin_residence = getResidence(loc);
        boolean plugin_kingdomsx = getKingdomsx(loc);
        boolean plugin_hClaims = gethClaims(loc);
        boolean plugin_griefDefender = getGriefDefender(loc);
        boolean plugin_ultimateClaims = getUltimateClaims(loc);
        boolean plugin_pueblos = getPueblos(loc);
        return  plugin_worldguard
                && plugin_griefPrevention
                && plugin_towny
                && plugin_redProtect
                && plugin_factionsUUID
                && plugin_lands
                && plugin_residence
                && plugin_kingdomsx
                && plugin_hClaims
                && plugin_griefDefender
                && plugin_ultimateClaims
                && plugin_pueblos;
    }

    // TESTED (v2.12.3)
    // Worldguard (v7.0.4 B1), WorldEdit (v7.2.0 B5)
    // https://dev.bukkit.org/projects/worldguard
    private boolean getWorlguard(Location loc) {
        boolean result = true;
        if (getDepends().isWorldguard())
            try {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
                //for (ProtectedRegion region : set.getRegions()) {
                 //   region.getId()
                //}
                result = set.size() == 0;
            } catch (Exception e) {
               e.printStackTrace();
            }
        return result;
    }

    // TESTED (v2.13.0)
    // GriefPrevention (v16.15.0)
    // https://www.spigotmc.org/resources/griefprevention.1884/
    private boolean getGriefprevention(Location loc) {
        boolean result = true;
        if (getDepends().isGriefprevention())
            try {
                result = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (2.13.0)
    // Towny (v0.96.1.11)
    // https://www.spigotmc.org/resources/towny.72694/
    private boolean getTowny(Location loc) {
        boolean result = true;
        if (getDepends().isTowny())
            try {
                result = TownyAPI.getInstance().isWilderness(loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // TESTED (2.13.0)
    // RedProtect (v7.7.2)
    // https://www.spigotmc.org/resources/redprotect.15841/
    private boolean getRedProtect(Location loc) {
        boolean result = true;
        if (getDepends().isRedProtect())
            try {
                result = RedProtect.get().getAPI().getRegion(loc) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (2.13.2)
    // FactionsUUID (v1.6.9.5-U0.5.16)
    // https://www.spigotmc.org/resources/factionsuuid.1035/
    private boolean getFactionsUUID(Location loc) {
        boolean result = true;
        if (getDepends().isFactionsUUID())
            try {
                Faction faction = Board.getInstance().getFactionAt(new FLocation(loc));
                result = faction.isWilderness() || faction.isSafeZone();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (2.14.3)
    // Lands (v5.0.5)
    // https://www.spigotmc.org/resources/lands.53313/
    private boolean getLands(Location loc) {
        boolean result = true;
        if (getDepends().isLands())
            try {
                result = !(new LandsIntegration(BetterRTP.getInstance()).isClaimed(loc));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (2.14.3)
    // Residence (v4.9.1.9)
    // https://www.spigotmc.org/resources/residence.11480/
    private boolean getResidence(Location loc) {
        boolean result = true;
        if (getDepends().isResidence())
            try {
                result = Residence.getInstance().getResidenceManager().getByLoc(loc) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (3.0.2)
    // KingdomsX (v1.10.5.2)
    // https://www.spigotmc.org/resources/kingdomsx.77670/
    private boolean getKingdomsx(Location loc) {
        boolean result = true;
        if (getDepends().isKingdomsX())
            try {
                org.kingdoms.constants.land.Land land = org.kingdoms.constants.land.Land.getLand(loc);
                result = land == null || !land.isClaimed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (3.1.0)
    // hClaims (v1.1.1)
    // https://www.spigotmc.org/resources/hclaims.90540/ (Local Repo)
    private boolean gethClaims(Location loc) {
        boolean result = true;
        if (getDepends().ishClaims())
            try {
                result = ClaimAPI.getInstance().isClaimed(loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (3.1.0)
    // GriefDefender (v1.5.10)
    // https://www.spigotmc.org/resources/griefdefender.68900/
    private boolean getGriefDefender(Location loc) {
        boolean result = true;
        if (getDepends().isGriefDefender())
            try {
                for (Claim claim : GriefDefender.getCore().getAllClaims())
                    if (claim.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                        result = false;
                        break;
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (3.1.0)
    // UltimateClaims (v1.6.1)
    // https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65
    private boolean getUltimateClaims(Location loc) {
        boolean result = true;
        if (getDepends().isUltimateClaims())
            try {
                result = UltimateClaims.getInstance().getClaimManager().getClaim(loc.getChunk()) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    // NOT TESTED (3.1.0)
    // Pueblos (v2.0.1)
    // https://www.spigotmc.org/resources/pueblos.91255/
    private boolean getPueblos(Location loc) {
        boolean result = true;
        if (getDepends().isPueblos())
            try {
                result = Pueblos.getInstance().getClaimHandler().getClaimMain(loc) == null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }

    private SoftDepends getDepends() {
        return BetterRTP.getInstance().getSettings().getsDepends();
    }
}
