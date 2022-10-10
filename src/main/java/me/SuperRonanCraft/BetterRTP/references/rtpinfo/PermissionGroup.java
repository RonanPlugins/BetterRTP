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
    @Getter private final HashMap<String, WorldPermissionGroup> worlds = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public PermissionGroup(Map.Entry<?, ?> fields) {
        this.groupName = fields.getKey().toString();

        BetterRTP.debug("- Permission Group: " + groupName);
        //Find Location and cache its values
        Object value = fields.getValue();
        for (Object worldList : ((ArrayList) value)) {
            for (Object hash : ((HashMap) worldList).entrySet()) {
                Map.Entry worldFields = (Map.Entry) hash;
                BetterRTP.debug("- -- World: " + worldFields.getKey());
                World world = Bukkit.getWorld(worldFields.getKey().toString());
                if (world != null) {
                    WorldPermissionGroup permissionGroup = new WorldPermissionGroup(groupName, world, worldFields);
                    this.worlds.put(worldFields.getKey().toString(), permissionGroup);
                } else
                    BetterRTP.debug("- - The Permission Group '" + groupName + "'s world '" + worldFields.getKey() + "' does not exist! Permission Group not loaded...");
            }
        }
    }

}
