package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_SettingUpEvent;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RTP {

    final RTPTeleport teleport = new RTPTeleport();
    final RTPPluginValidation softDepends = new RTPPluginValidation();
    public final RTPPermissionGroup permConfig = new RTPPermissionGroup();
    //Cache
    public final HashMap<String, RTPWorld> customWorlds = new HashMap<>();
    public final HashMap<String, String> overriden = new HashMap<>();
    public final WorldDefault defaultWorld = new WorldDefault();
    @Getter List<String> disabledWorlds, blockList;
    int maxAttempts, delayTime;
    boolean cancelOnMove, cancelOnDamage;
    public final HashMap<String, WORLD_TYPE> world_type = new HashMap<>();
    public final HashMap<String, RTPWorld> worldLocations = new HashMap<>();

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
        //CustomWorlds
        loadCustomWorlds();
        //Locations
        RTPLoader.loadWorldLocations(worldLocations);
        teleport.load(); //Load teleporting stuff
        permConfig.load(); //Load permission configs
    }

    public void loadCustomWorlds() { //Keeping this here because of the edit command
        RTPLoader.loadCustomWorlds(defaultWorld, customWorlds);
    }

    public WorldPlayer getPlayerWorld(RTPSetupInformation setup_info) {
        WorldPlayer pWorld = new WorldPlayer(setup_info.getPlayer(), Bukkit.getWorld(setup_info.getWorld()));

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
            //Custom World
            if (customWorlds.containsKey(setup_info.getWorld())) {
                RTPWorld cWorld = customWorlds.get(pWorld.getWorld().getName());
                pWorld.setup(null, cWorld, setup_info.getBiomes(), setup_info.isPersonalized());
            }
            //Default World
            else
                pWorld.setup(null, defaultWorld, setup_info.getBiomes(), setup_info.isPersonalized());
        }
        //World type
        WORLD_TYPE world_type;
        if (this.world_type.containsKey(setup_info.getWorld()))
            world_type = this.world_type.get(setup_info.getWorld());
        else {
            world_type = WORLD_TYPE.NORMAL;
            this.world_type.put(setup_info.getWorld(), world_type); //Defaults this so the error message isn't spammed
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
        if (setup.isCancelled()) {
            return;
        }

        String world_name = setup_info.getWorld();
        Player p = setup_info.getPlayer();
        CommandSender sendi = setup_info.getSender();

        // Locations
        if (setup_info.getLocation() != null) {
            WorldLocations location = setup_info.getLocation();
            world_name = location.getWorld().getName();
            setup_info.setWorld(world_name);
            setup_info.setBiomes(location.getBiomes());
        }

        // Check overrides
        if (world_name == null) {
            world_name = p.getWorld().getName();
        } else { // Check if nulled or world doesnt exist
            World _world = Bukkit.getWorld(world_name);
            if (_world == null) { //Check if world has spaces instead of underscores
                _world = Bukkit.getWorld(world_name.replace("_", " "));
                world_name = world_name.replace("_", "");
            }
            if (_world == null) {
                getPl().getText().getNotExist(sendi, world_name);
                return;
            }
        }
        if (overriden.containsKey(world_name)) {
            world_name = overriden.get(world_name);
            setup_info.setWorld(world_name);
        }
        // Not forced and has 'betterrtp.world.<world>'
        if (sendi == p && !getPl().getPerms().getAWorld(sendi, world_name)) {
            getPl().getText().getNoPermissionWorld(p, world_name);
            return;
        }
        // Check disabled worlds
        if (disabledWorlds.contains(world_name)) {
            getPl().getText().getDisabledWorld(sendi, world_name);
            return;
        }
        WorldPlayer pWorld = getPlayerWorld(setup_info);
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
            getPl().getCooldowns().add(p);
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