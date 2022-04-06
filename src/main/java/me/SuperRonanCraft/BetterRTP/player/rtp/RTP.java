package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdInfo;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdWorld;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_SettingUpEvent;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.PermissionGroup;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RTP {

    final RTPTeleport teleport = new RTPTeleport();
    final RTPPluginValidation softDepends = new RTPPluginValidation();
    //public final WorldPermissionGroup permConfig = new WorldPermissionGroup();
    //Cache
    public final HashMap<String, String> overriden = new HashMap<>();
    public final WorldDefault defaultWorld = new WorldDefault();
    @Getter List<String> disabledWorlds, blockList;
    int maxAttempts, delayTime;
    boolean cancelOnMove, cancelOnDamage;
    public final HashMap<String, WORLD_TYPE> world_type = new HashMap<>();
    //Worlds
    public final HashMap<String, RTPWorld> customWorlds = new HashMap<>();
    public final HashMap<String, RTPWorld> worldLocations = new HashMap<>();
    public final HashMap<String, PermissionGroup> worldPermissionGroups = new HashMap<>();

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
        RTPLoader.loadWorlds(defaultWorld, customWorlds);
        //Locations
        RTPLoader.loadLocations(worldLocations);
        //Permissions
        RTPLoader.loadPermissionGroups(worldPermissionGroups);
        teleport.load(); //Load teleporting stuff
        //permConfig.load(); //Load permission configs
    }

    public void loadWorlds() { //Keeping this here because of the edit command
        RTPLoader.loadWorlds(defaultWorld, customWorlds);
    }

    public void loadLocations() { //Keeping this here because of the edit command
        RTPLoader.loadLocations(worldLocations);
    }

    public void loadPermissionGroups() { //Keeping this here because of the edit command
        RTPLoader.loadPermissionGroups(worldPermissionGroups);
    }

    public WorldPlayer getPlayerWorld(RTPSetupInformation setup_info) {
        WorldPlayer pWorld = new WorldPlayer(setup_info.getPlayer(), setup_info.getWorld());

        //Random Location
        if (setup_info.getLocation() == null && BetterRTP.getInstance().getSettings().isUseLocationIfAvailable()) {
            setup_info.setLocation(HelperRTP.getRandomLocation(setup_info.getSender(), setup_info.getWorld()));
            if (setup_info.getLocation() == null)
                BetterRTP.getInstance().getLogger().warning("UseLocationIfAvailable is set to `true`, but no location was found for "
                        + setup_info.getSender().getName() + "! Using world defaults!");
        }
        //Location
        if (setup_info.getLocation() != null) {
            String setup_name = null;
            for (Map.Entry<String, RTPWorld> location_set : worldLocations.entrySet()) {
                RTPWorld location = location_set.getValue();
                if (location == setup_info.getLocation()) {
                    setup_name = location_set.getKey();
                    break;
                }
            }
            pWorld.setup(setup_name, setup_info.getLocation(), setup_info.getLocation().getBiomes(), setup_info.isPersonalized());
        }

        if (!pWorld.isSetup()) {
            WorldPermissionGroup group = null;
            if (pWorld.getPlayer() != null)
                for (Map.Entry<String, PermissionGroup> permissionGroup : BetterRTP.getInstance().getRTP().worldPermissionGroups.entrySet()) {
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
                pWorld.setup(null, group, setup_info.getBiomes(), setup_info.isPersonalized());
                pWorld.config = group;
            }
            //Custom World
            else if (customWorlds.containsKey(setup_info.getWorld().getName())) {
                RTPWorld cWorld = customWorlds.get(pWorld.getWorld().getName());
                pWorld.setup(null, cWorld, setup_info.getBiomes(), setup_info.isPersonalized());
            }
            //Default World
            else
                pWorld.setup(null, defaultWorld, setup_info.getBiomes(), setup_info.isPersonalized());
        }
        //World type
        WORLD_TYPE world_type;
        if (this.world_type.containsKey(setup_info.getWorld().getName()))
            world_type = this.world_type.get(setup_info.getWorld().getName());
        else {
            world_type = WORLD_TYPE.NORMAL;
            this.world_type.put(setup_info.getWorld().getName(), world_type); //Defaults this so the error message isn't spammed
            getPl().getLogger().warning("Seems like the world `" + setup_info.getWorld() + "` does not have a `WorldType` declared. " +
                    "Please add/fix this in the config.yml file! " +
                    "This world will be treated as an overworld!");
        }
        pWorld.setWorldtype(world_type);
        return pWorld;
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