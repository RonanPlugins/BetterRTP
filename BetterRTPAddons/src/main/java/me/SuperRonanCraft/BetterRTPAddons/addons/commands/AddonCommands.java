package me.SuperRonanCraft.BetterRTPAddons.addons.commands;

import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;

public class AddonCommands implements Addon {

    private final String name = "Commands";
    private final CommandsLoader loader = new CommandsLoader();

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        loader.load();
    }

    @Override
    public void unload() {
        loader.unload();
    }
}
