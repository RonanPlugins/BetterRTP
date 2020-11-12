package me.SuperRonanCraft.BetterRTPAddons;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface Addon extends Listener {

    boolean isEnabled();

    void load();

    default void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    default void disable() {
        HandlerList.unregisterAll(this);
    }

}
