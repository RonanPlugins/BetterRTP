package me.SuperRonanCraft.BetterRTP.references.settings;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

public class Settings {

    @Getter private boolean debug;
    @Getter private boolean delayEnabled;
    @Getter private int delayTime;
    @Getter private boolean rtpOnFirstJoin_Enabled;
    @Getter private String rtpOnFirstJoin_World;
    @Getter private boolean rtpOnFirstJoin_SetAsRespawn;
    @Getter private boolean statusMessages; //Send more information about rtp
    @Getter private int preloadRadius; //Amount of chunks to load around a safe rtp location (clamped (0 - 16))
    //Dependencies
    private final SoftDepends depends = new SoftDepends();
    public boolean protocolLibSounds;

    public void load() { //Load Settings
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        debug = config.getBoolean("Settings.Debugger");
        delayEnabled = config.getBoolean("Settings.Delay.Enabled");
        delayTime = config.getInt("Settings.Delay.Time");
        rtpOnFirstJoin_Enabled = config.getBoolean("Settings.RtpOnFirstJoin.Enabled");
        rtpOnFirstJoin_World = config.getString("Settings.RtpOnFirstJoin.World");
        rtpOnFirstJoin_SetAsRespawn = config.getBoolean("Settings.RtpOnFirstJoin.SetAsRespawn");
        preloadRadius = config.getInt("Settings.PreloadRadius");
        statusMessages = config.getBoolean("Settings.StatusMessages");
        protocolLibSounds = FileBasics.FILETYPE.EFFECTS.getBoolean("Sounds.ProtocolLibSound");
        depends.load();
    }

    public SoftDepends getsDepends() {
        return depends;
    }
}
