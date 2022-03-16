package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import me.SuperRonanCraft.BetterRTPAddons.util.ReloadListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private final AddonsHandler addonsHandler = new AddonsHandler();
    private final Files files = new Files();
    public final AddonMessages msgs = new AddonMessages();
    private final ReloadListener reloadListener = new ReloadListener();

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(reloadListener, this);
        load();
    }

    @Override
    public void onDisable() {
        addonsHandler.unload();
    }

    public void load() {
        files.load();
        addonsHandler.load();
    }

    public Files getFiles() {
        return files;
    }

    public AddonsHandler getAddonsHandler() {
        return addonsHandler;
    }

    public static Main getInstance() {
        return instance;
    }
}
