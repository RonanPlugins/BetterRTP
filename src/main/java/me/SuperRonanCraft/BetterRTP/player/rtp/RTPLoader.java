package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTPLoader {

    static void loadCustomWorlds(@NotNull WorldDefault defaultWorld, @NotNull HashMap<String, RTPWorld> customWorlds) {
        defaultWorld.setup();
        customWorlds.clear();
        try {
            FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
            List<Map<?, ?>> map = config.getMapList("CustomWorlds");
            for (Map<?, ?> m : map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    customWorlds.put(entry.getKey().toString(), new WorldCustom(entry.getKey().toString()));
                    if (getPl().getSettings().isDebug())
                        BetterRTP.debug("- Custom World '" + entry.getKey() + "' registered");
                }
        } catch (Exception e) {
            //No Custom Worlds
        }
    }

    static void loadOverrides(@NotNull HashMap<String, String> overriden) {
        overriden.clear();
        try {
            FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
            List<Map<?, ?>> override_map = config.getMapList("Overrides");
            for (Map<?, ?> m : override_map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    overriden.put(entry.getKey().toString(), entry.getValue().toString());
                    if (getPl().getSettings().isDebug())
                        getPl().getLogger().info("- Override '" + entry.getKey() + "' -> '" + entry.getValue() + "' added");
                    if (Bukkit.getWorld(entry.getValue().toString()) == null)
                        getPl().getLogger().warning("The world `" + entry.getValue() + "` doesn't seem to exist! Please update `" + entry.getKey() + "'s` override! Maybe there are capital letters?");
                }
        } catch (Exception e) {
            //No Overrides
        }
    }

    static void loadWorldTypes(@NotNull HashMap<String, WORLD_TYPE> world_type) {
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
                                    "'. Valid ID's are: " + valids);
                            //Wrong rtp world type
                        }
                    }/* else {
                        if (getPl().getSettings().debug)
                            getPl().getLogger().info("- World Type failed for '" + entry.getKey() + "' is it loaded?");
                    }*/
                }
            if (getPl().getSettings().isDebug())
                for (String world : world_type.keySet())
                    BetterRTP.debug("- World Type for '" + world + "' set to '" + world_type.get(world) + "'");
        } catch (Exception e) {
            e.printStackTrace();
            //No World Types
        }
    }

    static void loadWorldLocations(@NotNull HashMap<String, RTPWorld> worlds) {
        worlds.clear();
        FileBasics.FILETYPE config = FileBasics.FILETYPE.LOCATIONS;
        if (!config.getBoolean("Enabled"))
            return;
        List<Map<?, ?>> map = config.getMapList("Locations");
        for (Map<?, ?> m : map)
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                WorldLocations location = new WorldLocations(entry.getKey().toString());
                if (location.isValid()) {
                    worlds.put(entry.getKey().toString(), location);
                    if (getPl().getSettings().isDebug())
                        BetterRTP.debug("- Location '" + entry.getKey() + "' registered");
                }
            }
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
