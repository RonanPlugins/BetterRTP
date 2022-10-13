package me.SuperRonanCraft.BetterRTP.references.settings;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;

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
    @Getter private boolean protocolLibSounds;
    @Getter private boolean useLocationIfAvailable;
    @Getter private boolean locationNeedPermission;
    @Getter private boolean useLocationsInSameWorld;
    @Getter private boolean permissionGroupEnabled;
    @Getter private boolean queueEnabled;
    //Placeholders
    @Getter private String placeholder_true;
    @Getter private String placeholder_nopermission;
    @Getter private String placeholder_cooldown;
    @Getter private String placeholder_balance;
    @Getter private String placeholder_hunger;
    @Getter private String placeholder_timeDays;
    @Getter private String placeholder_timeHours;
    @Getter private String placeholder_timeMinutes;
    @Getter private String placeholder_timeSeconds;
    @Getter private String placeholder_timeZero;


    public void load() { //Load Settings
        FileOther.FILETYPE config = FileOther.FILETYPE.CONFIG;
        debug = config.getBoolean("Settings.Debugger");
        delayEnabled = config.getBoolean("Settings.Delay.Enabled");
        delayTime = config.getInt("Settings.Delay.Time");
        rtpOnFirstJoin_Enabled = config.getBoolean("Settings.RtpOnFirstJoin.Enabled");
        rtpOnFirstJoin_World = config.getString("Settings.RtpOnFirstJoin.World");
        rtpOnFirstJoin_SetAsRespawn = config.getBoolean("Settings.RtpOnFirstJoin.SetAsRespawn");
        preloadRadius = config.getInt("Settings.PreloadRadius");
        statusMessages = config.getBoolean("Settings.StatusMessages");
        permissionGroupEnabled = config.getBoolean("PermissionGroup.Enabled");
        queueEnabled = config.getBoolean("Settings.Queue.Enabled");
        protocolLibSounds = FileOther.FILETYPE.EFFECTS.getBoolean("Sounds.ProtocolLibSound");
        useLocationIfAvailable = FileOther.FILETYPE.LOCATIONS.getBoolean("UseLocationIfAvailable");
        locationNeedPermission = FileOther.FILETYPE.LOCATIONS.getBoolean("RequirePermission");
        useLocationsInSameWorld = FileOther.FILETYPE.LOCATIONS.getBoolean("UseLocationsInSameWorld");
        //Placeholders
        placeholder_true = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.CanRTP.Success");
        placeholder_nopermission = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.CanRTP.NoPermission");
        placeholder_cooldown = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.CanRTP.Cooldown");
        placeholder_balance = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.CanRTP.Price");
        placeholder_hunger = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.CanRTP.Hunger");
        placeholder_timeDays = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.TimeFormat.Days");
        placeholder_timeHours = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.TimeFormat.Hours");
        placeholder_timeMinutes = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.TimeFormat.Minutes");
        placeholder_timeSeconds = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.TimeFormat.Seconds");
        placeholder_timeZero = FileOther.FILETYPE.PLACEHOLDERS.getString("Config.TimeFormat.ZeroAll");
        depends.load();
    }

    public SoftDepends getsDepends() {
        return depends;
    }
}
