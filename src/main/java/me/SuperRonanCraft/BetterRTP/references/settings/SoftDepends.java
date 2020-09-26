package me.SuperRonanCraft.BetterRTP.references.settings;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SoftDepends {

    private boolean respect_worldguard = false;
    private boolean respect_griefprevention = false;
    private boolean respect_towny = false;
    private boolean respect_redProtect = false;
    private boolean respect_factionsUUID = false;
    //RETURNABLES
    private boolean worldguard = false;
    private boolean griefprevention = false;
    private boolean towny = false;
    private boolean redProtect = false;
    private boolean factionsUUID = false;

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

    void load() {
        FileBasics.FILETYPE config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        String pre = "Settings.Respect.";
        respect_worldguard = config.getBoolean(     pre + "WorldGuard");
        respect_griefprevention = config.getBoolean(pre + "GriefPrevention");
        respect_towny = config.getBoolean(          pre + "Towny");
        respect_redProtect = config.getBoolean(     pre + "RedProtect");
        respect_factionsUUID = config.getBoolean(   pre + "FactionsUUID");
        registerWorldguard();
        registerGriefPrevention();
        registerTowny();
        registerRedProtect();
        registerFactionsUUID();
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

    private void debug(String str) {
        if (Main.getInstance().getSettings().debug)
            Main.getInstance().getLogger().log(Level.INFO, str);
    }
}
