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

    public static boolean checkLocation(Location loc) {
        for (REGIONPLUGINS validators : REGIONPLUGINS.values())
            if (!validators.getValidator().check(loc))
                return false;
        return true;
    }
}
