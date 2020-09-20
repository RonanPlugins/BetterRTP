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
    //RETURNABLES
    private boolean worldguard = false;
    private boolean griefprevention = false;
    private boolean towny = false;
    private boolean redProtect = false;

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

    void load() {
        FileBasics.FILETYPE config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        respect_worldguard = config.getBoolean("Settings.Respect.WorldGuard");
        respect_griefprevention = config.getBoolean("Settings.Respect.GriefPrevention");
        respect_towny = config.getBoolean("Settings.Respect.Towny");
        respect_redProtect = config.getBoolean("Settings.Respect.RedProtect");
        registerWorldguard();
        registerGriefPrevention();
        registerTowny();
        registerRedProject();
    }

    public void registerWorldguard() {
        worldguard = respect_worldguard && Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        debug("Registered `WorldGuard` = " + worldguard);
    }

    public void registerGriefPrevention() {
        griefprevention = respect_griefprevention && Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");
        debug("Registered `GriefPrevention` = " + griefprevention);
    }

    public void registerTowny() {
        towny = respect_towny && Bukkit.getPluginManager().isPluginEnabled("Towny");
        debug("Registered `Towny` = " + towny);
    }

    public void registerRedProject() {
        redProtect = respect_redProtect && Bukkit.getPluginManager().isPluginEnabled("RedProtect");
        debug("Registered `RedProtect` = " + redProtect);
    }

    private void debug(String str) {
        if (Main.getInstance().getSettings().debug)
            Main.getInstance().getLogger().log(Level.INFO, str);
    }
}
