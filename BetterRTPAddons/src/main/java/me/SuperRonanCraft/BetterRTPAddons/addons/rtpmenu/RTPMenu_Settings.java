package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;

public class RTPMenu_Settings {
    @Getter private int refresh, lines;
    @Getter private String title;
    @Getter private Boolean autoCenter;

    void load(String prefix) {
        refresh = Files.FILETYPE.CONFIG.getInt(prefix + ".AutoRefresh");
        lines = Files.FILETYPE.CONFIG.getInt(prefix + ".Lines");
        title = Files.FILETYPE.CONFIG.getString(prefix + ".Title");
        autoCenter = Files.FILETYPE.CONFIG.getBoolean(prefix + ".AutoCenter");
    }
}
