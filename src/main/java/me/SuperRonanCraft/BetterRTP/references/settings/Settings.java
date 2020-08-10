package me.SuperRonanCraft.BetterRTP.references.settings;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;

public class Settings {

    public boolean debug;
    public boolean delayEnabled;
    public boolean firstJoinRtp;
    //Dependencies
    private SoftDepends depends = new SoftDepends();

    public void load() { //Load Settings
        depends.load();
        FileBasics.FILETYPE config = getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        debug = config.getBoolean("Settings.Debugger");
        delayEnabled = config.getBoolean("Settings.Delay.Enabled");
        firstJoinRtp = config.getBoolean("Settings.FirstJoinRtp");
    }

    public SoftDepends getsDepends() {
        return depends;
    }

    private Main getPl() {
        return Main.getInstance();
    }
}
