package me.SuperRonanCraft.BetterRTP.references.settings;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SoftDepends {

    private boolean respect_worldguard = false;
    private boolean respect_griefprevention = false;
    private boolean respect_towny = false;
    private boolean respect_redProtect = false;
    private boolean respect_factionsUUID = false;
    private boolean respect_lands = false;
    private boolean respect_residence = false;
    //RETURNABLES
    private boolean worldguard = false;
    private boolean griefprevention = false;
    private boolean towny = false;
    private boolean redProtect = false;
    private boolean factionsUUID = false;
    private boolean lands = false;
    private boolean residence = false;

    public boolean isWorldguard() {
        return worldguard;
    }

    public boolean isGriefprevention() {
        return griefprevention;
    }

    public boolean isTowny() {
        return towny;
    }

    public boolean isRedProtect() {
        return redProtect;
    }

    public boolean isFactionsUUID() {
        return factionsUUID;
    }

    public boolean isLands() {
        return lands;
    }

    public boolean isResidence() {
        return residence;
    }

    void load() {
        FileBasics.FILETYPE config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        String pre = "Settings.Respect.";
        respect_worldguard = config.getBoolean(     pre + "WorldGuard");
        respect_griefprevention = config.getBoolean(pre + "GriefPrevention");
        respect_towny = config.getBoolean(          pre + "Towny");
        respect_redProtect = config.getBoolean(     pre + "RedProtect");
        respect_factionsUUID = config.getBoolean(   pre + "FactionsUUID");
        respect_lands = config.getBoolean(          pre + "Lands");
        respect_residence = config.getBoolean(      pre + "Residence");
        registerWorldguard();
        registerGriefPrevention();
        registerTowny();
        registerRedProtect();
        registerFactionsUUID();
        registerLands();
        registerResidence();
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

    private void debug(String str) {
        if (Main.getInstance().getSettings().debug)
            Main.getInstance().getLogger().log(Level.INFO, str);
    }
}
