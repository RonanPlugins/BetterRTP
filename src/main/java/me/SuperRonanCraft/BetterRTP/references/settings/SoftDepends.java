package me.SuperRonanCraft.BetterRTP.references.settings;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SoftDepends {

    private boolean
            respect_worldguard = false,
            respect_griefprevention = false,
            respect_towny = false,
            respect_redProtect = false,
            respect_factionsUUID = false,
            respect_lands = false,
            respect_residence = false,
            respect_kingdomsx = false,
            respect_hClaims = false,
            respect_griefDefender = false,
            respect_ultimateClaims = false,
            respect_pueblos = false,
            respect_saberFactions = false;
    //RETURNABLES
    @Getter private boolean
            worldguard = false,
            griefprevention = false,
            towny = false,
            redProtect = false,
            factionsUUID = false,
            lands = false,
            residence = false,
            kingdomsx = false,
            hClaims = false,
            griefDefender = false,
            ultimateClaims = false,
            pueblos = false,
            saberFactions = false;

    void load() {
        FileBasics.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        String pre = "Settings.Respect.";
        respect_worldguard = config.getBoolean(         pre + "WorldGuard");
        respect_griefprevention = config.getBoolean(    pre + "GriefPrevention");
        respect_towny = config.getBoolean(              pre + "Towny");
        respect_redProtect = config.getBoolean(         pre + "RedProtect");
        respect_factionsUUID = config.getBoolean(       pre + "FactionsUUID");
        respect_lands = config.getBoolean(              pre + "Lands");
        respect_residence = config.getBoolean(          pre + "Residence");
        respect_kingdomsx = config.getBoolean(          pre + "KingdomsX");
        respect_hClaims = config.getBoolean(            pre + "hClaims");
        respect_griefDefender = config.getBoolean(      pre + "GriefDefender");
        respect_ultimateClaims = config.getBoolean(     pre + "UltimateClaims");
        respect_pueblos = config.getBoolean(            pre + "Pueblos");
        respect_saberFactions = config.getBoolean(      pre + "SaberFactions");
        registerWorldguard();
        registerGriefPrevention();
        registerTowny();
        registerRedProtect();
        registerFactionsUUID();
        registerLands();
        registerResidence();
        registerKingdomsX();
        registerClaimAPIPandomim();
        registerGriefDefender();
        registerUltimateClaims();
        registerPueblos();
        registerSaberFactions();
    }

    public void registerWorldguard() {
        worldguard = respect_worldguard && Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        if (respect_worldguard)
            debug("Respecting `WorldGuard` was " + (worldguard ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerGriefPrevention() {
        griefprevention = respect_griefprevention && Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");
        if (respect_griefprevention)
            debug("Respecting `GriefPrevention` was " + (griefprevention ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerTowny() {
        towny = respect_towny && Bukkit.getPluginManager().isPluginEnabled("Towny");
        if (respect_towny)
            debug("Respecting `Towny` was " + (towny ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerRedProtect() {
        redProtect = respect_redProtect && Bukkit.getPluginManager().isPluginEnabled("RedProtect");
        if (respect_redProtect)
            debug("Respecting `RedProtect` was " + (redProtect ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerFactionsUUID() {
        factionsUUID = respect_factionsUUID && Bukkit.getPluginManager().isPluginEnabled("Factions");
        if (respect_factionsUUID)
            debug("Respecting `FactionsUUID` was " + (factionsUUID ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerLands() {
        lands = respect_lands && Bukkit.getPluginManager().isPluginEnabled("Lands");
        if (respect_lands)
            debug("Respecting `Lands` was " + (lands ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerResidence() {
        residence = respect_residence && Bukkit.getPluginManager().isPluginEnabled("Residence");
        if (respect_residence)
            debug("Respecting `Residence` was " + (residence ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerKingdomsX() {
        kingdomsx = respect_kingdomsx && Bukkit.getPluginManager().isPluginEnabled("Kingdoms");
        if (respect_kingdomsx)
            debug("Respecting `KingdomsX` was " + (kingdomsx ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerClaimAPIPandomim() {
        hClaims = respect_hClaims && Bukkit.getPluginManager().isPluginEnabled("hClaim");
        if (respect_hClaims)
            debug("Respecting `hClaims` was " + (hClaims ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerGriefDefender() {
        griefDefender = respect_griefDefender && Bukkit.getPluginManager().isPluginEnabled("GriefDefender");
        if (respect_griefDefender)
            debug("Respecting `GriefDefender` was " + (griefDefender ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerUltimateClaims() {
        ultimateClaims = respect_ultimateClaims && Bukkit.getPluginManager().isPluginEnabled("UltimateClaims");
        if (respect_ultimateClaims)
            debug("Respecting `UltimateClaims` was " + (ultimateClaims ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerPueblos() {
        pueblos = respect_pueblos && Bukkit.getPluginManager().isPluginEnabled("Pueblos");
        if (respect_pueblos)
            debug("Respecting `Pueblos` was " + (pueblos ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    public void registerSaberFactions() {
        saberFactions = respect_saberFactions && Bukkit.getPluginManager().isPluginEnabled("Factions");
        if (respect_saberFactions)
            debug("Respecting `SaberFactions` was " + (saberFactions ? "SUCCESSFULLY" : "NOT") + " registered");
    }

    private void debug(String str) {
        if (BetterRTP.getInstance().getSettings().isDebug())
            BetterRTP.getInstance().getLogger().log(Level.INFO, str);
    }
}
