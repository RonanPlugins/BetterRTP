package me.SuperRonanCraft.BetterRTP.references.settings;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;

public class Settings {

    public boolean debug = false;

    private SoftDepends depends = new SoftDepends();

    public void load() { //Load Settings
        depends.load();
        debug = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG).getBoolean("Settings.Debugger");
    }

    public SoftDepends getsDepends() {
        return depends;
    }

}
