package me.SuperRonanCraft.BetterRTPAddons.addons.interfaces;

import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;

public class AddonInterface implements Addon {

    private String name = "Interface";

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + "Enabled");
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
