package me.SuperRonanCraft.BetterRTPAddons;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private final Events events = new Events();

    @Override
    public void onEnable() {
        instance = this;
        load();
    }

    void load() {
        events.load();
    }

    public static Main getInstance() {
        return instance;
    }
}
