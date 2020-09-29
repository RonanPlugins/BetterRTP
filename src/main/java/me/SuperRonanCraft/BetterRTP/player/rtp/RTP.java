package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTP {

    final RTPTeleport teleport = new RTPTeleport();
    final RTPPluginValidation softDepends = new RTPPluginValidation();
    public final RTPPermissionGroup permConfig = new RTPPermissionGroup();
    //Cache
    public HashMap<String, RTPWorld> customWorlds = new HashMap<>();
    public HashMap<String, String> overriden = new HashMap<>();
    public WorldDefault defaultWorld = new WorldDefault();
    List<String> disabledWorlds, blockList;
    int maxAttempts, delayTime;
    boolean cancelOnMove, cancelOnDamage;
    public HashMap<String, WORLD_TYPE> world_type = new HashMap<>();

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
        //OVER-RIDES
        try {
            overriden.clear();
            List<Map<?, ?>> override_map = config.getMapList("Overrides");
            for (Map<?, ?> m : override_map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    overriden.put(entry.getKey().toString(), entry.getValue().toString());
                    if (getPl().getSettings().debug)
                        getPl().getLogger().info("- Override '" + entry.getKey() + "' -> '" + entry.getValue() + "' added");
                }
        } catch (Exception e) {
            //No Overrides
        }

        try {
            world_type.clear();
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
                    Main.debug("- World Type for '" + world + "' set to '" + world_type.get(world) + "'");
        } catch (Exception e) {
            e.printStackTrace();
            //No World Types
        }

        loadWorldSettings();
        teleport.load(); //Load teleporting stuff
        permConfig.load(); //Load permission configs
    }

    public void loadWorldSettings() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        defaultWorld.setup();
        //CUSTOM WORLDS
        try {
            customWorlds.clear();
            List<Map<?, ?>> map = config.getMapList("CustomWorlds");
            for (Map<?, ?> m : map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    customWorlds.put(entry.getKey().toString(), new WorldCustom(entry.getKey().toString()));
                    if (getPl().getSettings().debug)
                        Main.debug("- Custom World '" + entry.getKey() + "' registered");
                }
        } catch (Exception e) {
            //No Custom Worlds
        }
    }

    public List<String> disabledWorlds() {
        return disabledWorlds;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public WorldPlayer getPlayerWorld(CommandSender p, String worldName, List<String> biomes, boolean personal) {
        WorldPlayer pWorld = new WorldPlayer(p, Bukkit.getWorld(worldName));
        // Set all methods
        if (customWorlds.containsKey(worldName)) {
            RTPWorld cWorld = customWorlds.get(pWorld.getWorld().getName());
            pWorld.setup(cWorld, cWorld.getPrice(), biomes, personal);
        } else
            pWorld.setup(defaultWorld, defaultWorld.getPrice(), biomes, personal);
        //World type
        WORLD_TYPE world_type = WORLD_TYPE.NORMAL; //World rtp type
        if (this.world_type.containsKey(worldName))
            world_type = this.world_type.get(worldName);
        pWorld.setWorldtype(world_type);
        return pWorld;
    }

    private Main getPl() {
        return Main.getInstance();
    }

    public void start(Player p, CommandSender sendi, String world_name, List<String> biomes, boolean delay) {
        new RTPPlayer(p, this).teleport(sendi, world_name, biomes, delay);
    }
}