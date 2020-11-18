package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTPAddons.util.Files;

public interface Addon {

    //Addon will check if it can be enabled
    boolean isEnabled();

    //Load the addon if enabled
    void load();

    //Unload the addon if enabled
    void unload();

    //Ability to grab a file
    default Files.FILETYPE getFile(Files.FILETYPE filetype) {
        return Main.getInstance().getFiles().getType(filetype);
    }

}
