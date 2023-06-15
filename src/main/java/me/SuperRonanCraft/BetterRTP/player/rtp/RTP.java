package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.WarningHandler;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_SettingUpEvent;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Check;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.PermissionGroup;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTP {

    final RTPTeleport teleport = new RTPTeleport();
    //Cache
    public final HashMap<String, String> overriden = new HashMap<>();
    @Getter List<String> disabledWorlds, blockList;
    int maxAttempts, delayTime;
    boolean cancelOnMove, cancelOnDamage;
    public final HashMap<String, WORLD_TYPE> world_type = new HashMap<>();
    //Worlds
    @Getter private final WorldDefault RTPdefaultWorld = new WorldDefault();
    @Getter private final HashMap<String, RTPWorld> RTPcustomWorld = new HashMap<>();
    @Getter private final HashMap<String, RTPWorld> RTPworldLocations = new HashMap<>();
    @Getter private final HashMap<String, PermissionGroup> permissionGroups = new HashMap<>();

    public RTPTeleport getTeleport() {
        return teleport;
    }

    public void load() {
        FileOther.FILETYPE config = FileOther.FILETYPE.CONFIG;
        disabledWorlds = config.getStringList("DisabledWorlds");
        maxAttempts = config.getInt("Settings.MaxAttempts");
        delayTime = config.getInt("Settings.Delay.Time");
        cancelOnMove = config.getBoolean("Settings.Delay.CancelOnMove");
        cancelOnDamage = config.getBoolean("Settings.Delay.CancelOnDamage");
        blockList = config.getStringList("BlacklistedBlocks");
        //Overrides
        RTPLoader.loadOverrides(overriden);
        //WorldType
        RTPLoader.loadWorldTypes(world_type);
        //Worlds & CustomWorlds
        loadWorlds();
        //Locations
        loadLocations();
        //Permissions
        loadPermissionGroups();
        teleport.load(); //Load teleporting stuff
    }

    public void loadWorlds() { //Keeping this here because of the edit command
        RTPLoader.loadWorlds(RTPdefaultWorld, RTPcustomWorld);
    }

    public void loadLocations() { //Keeping this here because of the edit command
        RTPLoader.loadLocations(RTPworldLocations);
    }

    public void loadPermissionGroups() { //Keeping this here because of the edit command
        RTPLoader.loadPermissionGroups(permissionGroups);
    }

    public void start(RTPSetupInformation setup_info) {
        start(HelperRTP.getPlayerWorld(setup_info));
    }

    public void start(WorldPlayer pWorld) {
        RTP_SettingUpEvent setup = new RTP_SettingUpEvent(pWorld.getPlayer());
        Bukkit.getPluginManager().callEvent(setup);
        if (setup.isCancelled())
            return;
        rtp(pWorld.getSendi(), pWorld, pWorld.getRtp_type());
    }

    private void rtp(CommandSender sendi, WorldPlayer pWorld, RTP_TYPE type) {
        //Cooldown
        Player p = pWorld.getPlayer();
        getPl().getpInfo().getRtping().put(p, true); //Cache player so they cant run '/rtp' again while rtp'ing
        //Setup player rtp methods
        RTPPlayer rtpPlayer = new RTPPlayer(p, this, pWorld, type);
        // Delaying? Else, just go
        if (pWorld.getPlayerInfo().applyDelay && HelperRTP_Check.applyDelay(pWorld.getPlayer())) {
            new RTPDelay(sendi, rtpPlayer, delayTime, cancelOnMove, cancelOnDamage);
        } else {
            if (!teleport.beforeTeleportInstant(sendi, p))
                rtpPlayer.randomlyTeleport(sendi);
        }
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}