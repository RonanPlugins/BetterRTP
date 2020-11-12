package me.SuperRonanCraft.BetterRTPAddons.interfaces;

import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;

public class AddonInterface implements Addon {

    @Override
    public boolean isEnabled() {
        return Files.FILETYPE.INTERFACE.getBoolean("Enabled");
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
