package me.SuperRonanCraft.BetterRTP.references.settings;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Bukkit;

public class SoftDepends {

    private boolean worldguard = false, griefprevention = false, savagefactions = false;

    public boolean isWorldguard() {
        return worldguard;
    }

    public boolean isGriefprevention() {
        return griefprevention;
    }

    void load() {
        FileBasics.FILETYPE config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        if (config.getBoolean("Settings.RespectWorldGuard"))
            registerWorldguard();
        else if (worldguard)
            worldguard = false;
        if (config.getBoolean("Settings.RespectGriefPrevention"))
            registerGriefPrevention();
        else if (griefprevention)
            griefprevention = false;
        if (config.getBoolean("Settings.RespectSavageFactions"))
            registerSavageFactions();
        else if (savagefactions)
            savagefactions = false;
    }

    private void registerWorldguard() {
        worldguard = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    }

    private void registerGriefPrevention() {
        griefprevention = Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");
    }

    private void registerSavageFactions() {
        savagefactions = Bukkit.getPluginManager().isPluginEnabled("Factions");
    }
}
