package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RTP {

    final RTPTeleport teleport = new RTPTeleport();
    final RTPPluginValidation softDepends = new RTPPluginValidation();
    public final RTPPermissionGroup permConfig = new RTPPermissionGroup();
    //Cache
    public HashMap<String, RTPWorld> customWorlds = new HashMap<>();
    public HashMap<String, String> overriden = new HashMap<>();
    public WorldDefault defaultWorld = new WorldDefault();
    public List<String> disabledWorlds, blockList;
    int maxAttempts, delayTime;
    boolean cancelOnMove, cancelOnDamage;
    public HashMap<String, WORLD_TYPE> world_type = new HashMap<>();
    public HashMap<String, RTPWorld> worldLocations = new HashMap<>();

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
        loadOverrides();
        //WorldType
        loadWorldTypes();
        //CustomWorlds
        loadCustomWorlds();
        //Locations
        loadWorldLocations();
        teleport.load(); //Load teleporting stuff
        permConfig.load(); //Load permission configs
    }

    private void loadOverrides() {
        overriden.clear();
        try {
            FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
            List<Map<?, ?>> override_map = config.getMapList("Overrides");
            for (Map<?, ?> m : override_map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    overriden.put(entry.getKey().toString(), entry.getValue().toString());
                    if (getPl().getSettings().debug)
                        getPl().getLogger().info("- Override '" + entry.getKey() + "' -> '" + entry.getValue() + "' added");
                    if (Bukkit.getWorld(entry.getValue().toString()) == null)
                        getPl().getLogger().warning("The world `" + entry.getValue() + "` doesn't seem to exist! Please update `" + entry.getKey() + "'s` override! Maybe there are capital letters?");
                }
        } catch (Exception e) {
            //No Overrides
        }
    }

    private void loadWorldTypes() {
        world_type.clear();
        try {
            FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
            for (World world : Bukkit.getWorlds())
                world_type.put(world.getName(), WORLD_TYPE.NORMAL);
            List<Map<?, ?>> world_map = config.getMapList("WorldType");
            for (Map<?, ?> m : world_map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    if (world_type.containsKey(entry.getKey())) {
                        try {
                            WORLD_TYPE type = WORLD_TYPE.valueOf(entry.getValue().toString().toUpperCase());
                            world_type.put(entry.getKey().toString(), type);
                        } catch(IllegalArgumentException e) {
                            StringBuilder valids = new StringBuilder();
                            for (WORLD_TYPE type : WORLD_TYPE.values())
                                valids.append(type.name()).append(", ");
                            valids.replace(valids.length() - 2, valids.length(), "");
                            getPl().getLogger().severe("World Type for '" + entry.getKey() + "' is INVALID '" + entry.getValue() +
                                    "'. Valid ID's are: " + valids.toString());
                            //Wrong rtp world type
                        }
                    }/* else {
                        if (getPl().getSettings().debug)
                            getPl().getLogger().info("- World Type failed for '" + entry.getKey() + "' is it loaded?");
                    }*/
                }
            if (getPl().getSettings().debug)
                for (String world : world_type.keySet())
                    BetterRTP.debug("- World Type for '" + world + "' set to '" + world_type.get(world) + "'");
        } catch (Exception e) {
            e.printStackTrace();
            //No World Types
        }
    }

    public void loadCustomWorlds() {
        defaultWorld.setup();
        customWorlds.clear();
        try {
            FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
            List<Map<?, ?>> map = config.getMapList("CustomWorlds");
            for (Map<?, ?> m : map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    customWorlds.put(entry.getKey().toString(), new WorldCustom(entry.getKey().toString()));
                    if (getPl().getSettings().debug)
                        BetterRTP.debug("- Custom World '" + entry.getKey() + "' registered");
                }
        } catch (Exception e) {
            //No Custom Worlds
        }
    }

    public void loadWorldLocations() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.LOCATIONS;
        worldLocations.clear();
        if (!config.getBoolean("Enabled"))
            return;
        List<Map<?, ?>> map = config.getMapList("Locations");
        for (Map<?, ?> m : map)
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                WorldLocations location = new WorldLocations(entry.getKey().toString());
                if (location.isValid()) {
                    worldLocations.put(entry.getKey().toString(), location);
                    if (getPl().getSettings().debug)
                        BetterRTP.debug("- Location '" + entry.getKey() + "' registered");
                }
            }
    }

    public List<String> disabledWorlds() {
        return disabledWorlds;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public WorldPlayer getPlayerWorld(RTPSetupInformation setup_info) {
        WorldPlayer pWorld = new WorldPlayer(setup_info.player, Bukkit.getWorld(setup_info.world));

        //Locations
        if (setup_info.location == null) {
            List<RTPWorld> locationsList = new ArrayList<>();
            for (Map.Entry<String, RTPWorld> location_set : worldLocations.entrySet()) {
                RTPWorld location = location_set.getValue();
                if (location.getWorld().getName().equals(setup_info.world))
                    locationsList.add(location_set.getValue());
            }
            if (!locationsList.isEmpty()) {
                RTPWorld location = locationsList.size() > 1 ? locationsList.get((new Random()).nextInt(locationsList.size() - 1)) : locationsList.get(0);

                setup_info.location = (WorldLocations) location;
            }
        }

        if (setup_info.location != null) {
            String setup_name = null;
            for (Map.Entry<String, RTPWorld> location_set : worldLocations.entrySet()) {
                RTPWorld location = location_set.getValue();
                if (location == setup_info.location) {
                    setup_name = location_set.getKey();
                    break;
                }
            }
            pWorld.setup(setup_name, setup_info.location, setup_info.location.getBiomes(), setup_info.personalized);
        }


        if (!pWorld.isSetup()) {
            //Custom World
            if (customWorlds.containsKey(setup_info.world)) {
                RTPWorld cWorld = customWorlds.get(pWorld.getWorld().getName());
                pWorld.setup(null, cWorld, setup_info.biomes, setup_info.personalized);
            }
            //Default World
            else
                pWorld.setup(null, defaultWorld, setup_info.biomes, setup_info.personalized);
        }
        //World type
        WORLD_TYPE world_type; //World rtp type
        if (this.world_type.containsKey(setup_info.world))
            world_type = this.world_type.get(setup_info.world);
        else {
            world_type = WORLD_TYPE.NORMAL;
            this.world_type.put(setup_info.world, world_type); //Defaults this so the error message isn't spammed
            getPl().getLogger().warning("Seems like the world `" + setup_info.world + "` does not have a `WorldType` declared. " +
                    "Please add/fix this in the config.yml file! " +
                    "This world will be treated as an overworld!");
        }
        pWorld.setWorldtype(world_type);
        return pWorld;
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }

    public void start(RTPSetupInformation setup_info) {
        String world_name = setup_info.world;
        Player p = setup_info.player;
        CommandSender sendi = setup_info.sender;

        // Locations
        if (setup_info.location != null) {
            WorldLocations location = setup_info.location;
            world_name = location.getWorld().getName();
            setup_info.world = world_name;
            setup_info.biomes = location.getBiomes();
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
            setup_info.world = world_name;
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
        //BetterRTP.getInstance().getpInfo().setRTPType(p, rtpType);
        rtp(sendi, pWorld, setup_info.delay, setup_info.rtp_type);
    }

    private void rtp(CommandSender sendi, WorldPlayer pWorld, boolean delay, RTP_TYPE type) {
        //Cooldown
        Player p = pWorld.getPlayer();
        getPl().getCmd().cooldowns.add(p.getUniqueId());
        getPl().getCmd().rtping.put(p.getUniqueId(), true); //Cache player so they cant run '/rtp' again while rtp'ing
        //Setup player rtp methods
        RTPPlayer rtpPlayer = new RTPPlayer(p, this, pWorld, type);
        // Delaying? Else, just go
        if (getPl().getSettings().delayEnabled && delay) {
            new RTPDelay(sendi, rtpPlayer, delayTime, cancelOnMove, cancelOnDamage);
        } else {
            teleport.beforeTeleportInstant(sendi, p);
            rtpPlayer.randomlyTeleport(sendi);
        }
    }
}