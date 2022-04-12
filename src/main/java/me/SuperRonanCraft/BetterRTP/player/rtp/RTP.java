package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.WarningHandler;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_SettingUpEvent;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.PermissionGroup;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RTP {

    final RTPTeleport teleport = new RTPTeleport();
    final RTPPluginValidation softDepends = new RTPPluginValidation();
    //public final WorldPermissionGroup permConfig = new WorldPermissionGroup();
    //Cache
    public final HashMap<String, String> overriden = new HashMap<>();
    @Getter List<String> disabledWorlds, blockList;
    int maxAttempts, delayTime;
    boolean cancelOnMove, cancelOnDamage;
    public final HashMap<String, WORLD_TYPE> world_type = new HashMap<>();
    //Worlds
    public final WorldDefault RTPdefaultWorld = new WorldDefault();
    public final HashMap<String, RTPWorld> RTPcustomWorld = new HashMap<>();
    public final HashMap<String, RTPWorld> RTPworldLocations = new HashMap<>();
    public final HashMap<String, PermissionGroup> permissionGroups = new HashMap<>();

    public RTPTeleport getTeleport() {
        return teleport;
    }

    public void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
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
        RTPLoader.loadWorlds(RTPdefaultWorld, RTPcustomWorld);
        //Locations
        RTPLoader.loadLocations(RTPworldLocations);
        //Permissions
        RTPLoader.loadPermissionGroups(permissionGroups);
        teleport.load(); //Load teleporting stuff
        //permConfig.load(); //Load permission configs
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

    public WorldPlayer getPlayerWorld(RTPSetupInformation setup_info) {
        WorldPlayer pWorld = new WorldPlayer(setup_info.getPlayer(), setup_info.getWorld());

        //Random Location
        if (setup_info.getLocation() == null && BetterRTP.getInstance().getSettings().isUseLocationIfAvailable()) {
            setup_info.setLocation(HelperRTP.getRandomLocation(setup_info.getSender(), setup_info.getWorld()));
            if (setup_info.getLocation() == null)
                WarningHandler.warn(WarningHandler.WARNING.USELOCATION_ENABLED_NO_LOCATION_AVAILABLE,
                    "This is not an error! UseLocationIfAvailable is set to `true`, but no location was found for "
                        + setup_info.getSender().getName() + "! Using world defaults! (Maybe they dont have permission?)");
        }
        //Location
        if (setup_info.getLocation() != null) {
            String setup_name = null;
            for (Map.Entry<String, RTPWorld> location_set : RTPworldLocations.entrySet()) {
                RTPWorld location = location_set.getValue();
                if (location == setup_info.getLocation()) {
                    setup_name = location_set.getKey();
                    break;
                }
            }
            pWorld.setup(setup_name, setup_info.getLocation(), setup_info.getLocation().getBiomes());
        }

        if (!pWorld.isSetup()) {
            WorldPermissionGroup group = null;
            if (pWorld.getPlayer() != null)
                for (Map.Entry<String, PermissionGroup> permissionGroup : BetterRTP.getInstance().getRTP().permissionGroups.entrySet()) {
                    for (Map.Entry<String, WorldPermissionGroup> worldPermission : permissionGroup.getValue().getWorlds().entrySet()) {
                        if (pWorld.getWorld().equals(worldPermission.getValue().getWorld())) {
                            if (PermissionNode.getPermissionGroup(pWorld.getPlayer(), permissionGroup.getKey())) {
                                if (group != null) {
                                    if (group.getPriority() < worldPermission.getValue().getPriority())
                                        continue;
                                }
                                group = worldPermission.getValue();
                            }
                        }
                    }
                }

            //Permission Group
            if (group != null) {
                pWorld.setup(null, group, setup_info.getBiomes());
                pWorld.config = group;
            }
            //Custom World
            else if (RTPcustomWorld.containsKey(setup_info.getWorld().getName())) {
                RTPWorld cWorld = RTPcustomWorld.get(pWorld.getWorld().getName());
                pWorld.setup(null, cWorld, setup_info.getBiomes());
            }
            //Default World
            else
                pWorld.setup(null, RTPdefaultWorld, setup_info.getBiomes());
        }
        //World type
        pWorld.setWorldtype(getWorldType(pWorld));
        return pWorld;
    }

    public static WORLD_TYPE getWorldType(RTPWorld pWorld) {
        WORLD_TYPE world_type;
        RTP rtp = BetterRTP.getInstance().getRTP();
        if (rtp.world_type.containsKey(pWorld.getWorld().getName()))
            world_type = rtp.world_type.get(pWorld.getWorld().getName());
        else {
            world_type = WORLD_TYPE.NORMAL;
            rtp.world_type.put(pWorld.getWorld().getName(), world_type); //Defaults this so the error message isn't spammed
            WarningHandler.warn(WarningHandler.WARNING.NO_WORLD_TYPE_DECLARED, "Seems like the world `" + pWorld.getWorld().getName() + "` does not have a `WorldType` declared. " +
                    "Please add/fix this in the config.yml file! This world will be treated as an overworld! " +
                    "If this world is a nether world, configure it to NETHER (example: `- " + pWorld.getWorld().getName() + ": NETHER`", false);
        }
        return world_type;
    }

    public void start(RTPSetupInformation setup_info) {
        RTP_SettingUpEvent setup = new RTP_SettingUpEvent(setup_info.getPlayer());
        Bukkit.getPluginManager().callEvent(setup);
        if (setup.isCancelled())
            return;

        CommandSender sendi = setup_info.getSender();

        WorldPlayer pWorld = getPlayerWorld(setup_info);
        //Debugging!
        //CmdInfo.sendInfoWorld(sendi, CmdInfo.infoGetWorld(sendi, setup_info.getWorld(), setup_info.getPlayer(), pWorld));
        // Economy
        if (!getPl().getEco().hasBalance(sendi, pWorld))
            return;
        rtp(sendi, pWorld, setup_info.isDelay(), setup_info.getRtp_type(), setup_info.isCooldown());
    }

    private void rtp(CommandSender sendi, WorldPlayer pWorld, boolean delay, RTP_TYPE type, boolean cooldown) {
        //Cooldown
        Player p = pWorld.getPlayer();
        //p.sendMessage("Cooling down: " + cooldown);
        if (cooldown)
            getPl().getCooldowns().add(p, pWorld.getWorld());
        getPl().getpInfo().getRtping().put(p, true); //Cache player so they cant run '/rtp' again while rtp'ing
        //Setup player rtp methods
        RTPPlayer rtpPlayer = new RTPPlayer(p, this, pWorld, type);
        // Delaying? Else, just go
        if (getPl().getSettings().isDelayEnabled() && delay) {
            new RTPDelay(sendi, rtpPlayer, delayTime, cancelOnMove, cancelOnDamage);
        } else {
            teleport.beforeTeleportInstant(sendi, p);
            rtpPlayer.randomlyTeleport(sendi);
        }
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}