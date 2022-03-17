package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPermissionGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionGroup {

    String groupName;
    @Getter private HashMap<String, WorldPermissionGroup> worlds = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public PermissionGroup(Map.Entry<?, ?> fields) {
        this.groupName = fields.getKey().toString();

        BetterRTP.debug("- Permission Group: " + groupName);
        //Find Location and cache its values
        Object value = fields.getValue();
        for (Object worldList : ((ArrayList) value)) {
            for (Object hash : ((HashMap) worldList).entrySet()) {
                Map.Entry world = (Map.Entry) hash;
                BetterRTP.debug("- -- World: " + world.getKey());
                WorldPermissionGroup permissionGroup = new WorldPermissionGroup(groupName, world.getKey().toString(), world);
                boolean loaded = false;
                for (World realWorld : Bukkit.getWorlds()) {
                    if (realWorld.getName().equals(permissionGroup.world)) {
                        this.worlds.put(world.getKey().toString(), permissionGroup);
                        loaded = true;
                        break;
                    }
                }
                if (!loaded) {
                    BetterRTP.debug("- - The Permission groups '" + groupName + "'s world '" + world.getKey() + "' does not exist! World info not loaded...");
                }
            }
        }
    }

}
