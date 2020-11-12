package me.SuperRonanCraft.BetterRTPAddons;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private final AddonsHandler addonsHandler = new AddonsHandler();
    private final Files files = new Files();

    @Override
    public void onEnable() {
        instance = this;
        load();
    }

    void load() {
        files.load();
        addonsHandler.load();
    }

    public static Main getInstance() {
        return instance;
    }
}
