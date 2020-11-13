package me.SuperRonanCraft.BetterRTPAddons.logger;

import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class AddonLogger implements Addon, Listener {

    @Override
    public boolean isEnabled() {
        return Files.FILETYPE.LOGGER.getBoolean("Enabled");
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

}
