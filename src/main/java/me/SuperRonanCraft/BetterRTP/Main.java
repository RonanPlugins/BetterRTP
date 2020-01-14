package me.SuperRonanCraft.BetterRTP;

import me.SuperRonanCraft.BetterRTP.player.Commands;
import me.SuperRonanCraft.BetterRTP.player.RTP;
import me.SuperRonanCraft.BetterRTP.player.events.Listener;
import me.SuperRonanCraft.BetterRTP.references.Econ;
import me.SuperRonanCraft.BetterRTP.references.Permissions;
import me.SuperRonanCraft.BetterRTP.references.Updater;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.file.Files;
import me.SuperRonanCraft.BetterRTP.references.file.Messages;
import me.SuperRonanCraft.BetterRTP.references.web.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class Main extends JavaPlugin {
    private Permissions perms = new Permissions();
    private Messages text = new Messages(this);
    private Econ eco = new Econ();
    private Commands cmd = new Commands(this);
    private RTP rtp = new RTP(this);
    private Listener listener = new Listener();
    private boolean worldguard = false, griefprevention = false, savagefactions = false;
    private static Main instance;
    private Files files = new Files();

    public void onEnable() {
        instance = this;
        new Updater(this);
        new Metrics(this);
        loadAll();
        listener.registerEvents(this);
    }

    public Files getFiles() {
        return files;
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sendi, Command cmd, String label, String[] args) {
        try {
            this.cmd.commandExecuted(sendi, label, args);
        } catch (NullPointerException e) {
            e.printStackTrace();
            text.error(sendi);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.cmd.onTabComplete(sender, args);
    }

    public Permissions getPerms() {
        return perms;
    }

    public Messages getText() {
        return text;
    }

    public Econ getEco() {
        return eco;
    }

    public Commands getCmd() {
        return cmd;
    }

    public RTP getRTP() {
        return rtp;
    }

    public boolean isWorldguard() {
        return worldguard;
    }

    public boolean isGriefprevention() {
        return griefprevention;
    }

    public void reload(CommandSender sendi) {
        loadAll();
        text.getReload(sendi);
    }

    //Load
    private void loadAll() {
        //recreatePermissions();
        //registerConfig(reload);
        files.loadAll();
        rtp.load();
        cmd.load();
        listener.load();
        loadSettings();
    }

    //private void registerConfig(boolean reload) {
        //if (reload)
        //    reloadConfig();
        //getConfig().options().copyDefaults(true);
        //saveConfig();
    //}

    private void loadSettings() {
        FileBasics.FILETYPE config = getFiles().getType(FileBasics.FILETYPE.CONFIG);
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

    private void recreatePermissions() {
        //Permissions File
        saveResource(new File(getDataFolder(), "permissions.yml").getName(), true);
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
