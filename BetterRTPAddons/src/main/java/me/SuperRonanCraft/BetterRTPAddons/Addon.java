package me.SuperRonanCraft.BetterRTPAddons;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface Addon extends Listener {

    //Addon will check if it can be enabled
    boolean isEnabled();

    //Load the addon if enabled
    void load();

    //Unload the addon if enabled
    void unload();

    default void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    default void unregister() {
        HandlerList.unregisterAll(this);
    }

}
