package me.SuperRonanCraft.BetterRTPAddons.addons.interfaces;

import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;

public class AddonInterface implements Addon {

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.INTERFACE).getBoolean("Enabled");
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
