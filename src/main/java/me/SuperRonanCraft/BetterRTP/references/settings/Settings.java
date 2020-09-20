package me.SuperRonanCraft.BetterRTP.references.settings;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;

public class Settings {

    public boolean debug;
    public boolean delayEnabled;
    public boolean rtpOnFirstJoin;
    public String rtpOnFirstJoinWorld;
    public int preloadRadius; //Amount of chunks to load around a safe rtp location (clamped (0 - 16))
    //Dependencies
    private final SoftDepends depends = new SoftDepends();

    public void load() { //Load Settings
        FileBasics.FILETYPE config = getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        debug = config.getBoolean("Settings.Debugger");
        delayEnabled = config.getBoolean("Settings.Delay.Enabled");
        rtpOnFirstJoin = config.getBoolean("Settings.RtpOnFirstJoin.Enabled");
        rtpOnFirstJoinWorld = config.getString("Settings.RtpOnFirstJoin.World");
        preloadRadius = config.getInt("Settings.PreloadRadius");
        depends.load();
    }

    public SoftDepends getsDepends() {
        return depends;
    }

    private Main getPl() {
        return Main.getInstance();
    }
}
