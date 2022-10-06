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
import me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins.*;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class RTPPluginValidation {

    boolean checkLocation(Location loc) {
        boolean plugin_worldGuard = RTP_WorldGuard.check(loc);
        boolean plugin_griefPrevention = RTP_GriefPrevention.check(loc);
        boolean plugin_towny = RTP_Towny.check(loc);
        boolean plugin_redProtect = RTP_RedProtect.check(loc);
        boolean plugin_factionsUUID = RTP_FactionsUUID.check(loc);
        boolean plugin_lands = RTP_Lands.check(loc);
        boolean plugin_residence = RTP_Residence.check(loc);
        boolean plugin_kingdomsx = RTP_KingdomsX.check(loc);
        boolean plugin_hClaims = RTP_hClaims.check(loc);
        boolean plugin_griefDefender = RTP_GriefDefender.check(loc);
        boolean plugin_ultimateClaims = RTP_UltimateClaims.check(loc);
        boolean plugin_pueblos = RTP_Pueblos.check(loc);
        boolean plugin_saberFactions = RTP_SaberFactions.check(loc);
        return  plugin_worldGuard
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
                && plugin_pueblos
                && plugin_saberFactions;
    }
}
