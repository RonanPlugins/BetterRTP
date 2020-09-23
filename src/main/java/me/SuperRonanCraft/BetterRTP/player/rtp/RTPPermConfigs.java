package me.SuperRonanCraft.BetterRTP.player.rtp;

import com.sun.org.apache.xerces.internal.xs.StringList;
import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("rawtypes")
public class RTPPermConfigs {
    private List<RTPPermConfiguration> groups = new ArrayList<>();

    public RTPPermConfiguration getGroup(Player p) {
        for (RTPPermConfiguration group : groups)
            if (Main.getInstance().getPerms().getConfig(p, group.name))
                return group;
        return null;
    }

    public void load() {
        for (RTPPermConfiguration group : groups)
            group.worlds.clear();
        groups.clear();
        YamlConfiguration config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG).getConfig();
        List<Map<?, ?>> list = config.getMapList("PermissionConfigs");
        for (Map<?, ?> m : list)
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                RTPPermConfiguration group = new RTPPermConfiguration(entry);
                if (group.isValid()) {
                    groups.add(group);
                    Main.debug("- Group " + group.name + " has " + group.worlds.size() + " worlds setup, permission: 'betterrtp.config." + group.name + "'");
                    for (RTPPermConfigurationWorld world : group.worlds) {
                        Main.debug("  - World '" + world.name + "' MaxRad = " + world.maxRad + ", MinRad = " + world.minRad);
                    }
                } else {
                    Main.debug("ERROR! Group " + group.name + " was not setup correctly!");
                }
            }
    }

    public static class RTPPermConfiguration {

        boolean valid;
        String name;
        List<RTPPermConfigurationWorld> worlds = new ArrayList<>();

        RTPPermConfiguration(Map.Entry<?, ?> fields) {
            String group = fields.getKey().toString();
            Object value = fields.getValue();
            for (Object worlds : ((ArrayList) value)) {
                for (Object hash : ((HashMap) worlds).entrySet()) {
                    RTPPermConfigurationWorld worldConfig = new RTPPermConfigurationWorld(hash, group);
                    if (worldConfig.isValid())
                        this.worlds.add(worldConfig);
                    else
                        Main.debug("ERROR! Group " + group + " world " + worldConfig.name + " was not setup correctly!");
                }
            }
            this.name = group;
            valid = worlds.size() > 0 && group != null;
        }

        boolean isValid() {
            return valid;
        }
    }

    public static class RTPPermConfigurationWorld {

        boolean valid = true;

        int maxRad = -1;
        int minRad = -1;
        int price = -1;
        String name;

        RTPPermConfigurationWorld(Object hash, String group) {
            Map.Entry world = (Map.Entry) hash;
            this.name = world.getKey().toString();
            //Main.getInstance().getLogger().info("World added to '" + group +"': '" + world.getKey() + "'");
            for (Object hash2 : ((HashMap) world.getValue()).entrySet()) {
                Map.Entry hash3 = (Map.Entry) hash2;
                String field = hash3.getKey().toString();
                if (field.equalsIgnoreCase("MaxRadius")) { //MaxRadius
                    maxRad = getInt(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("MinRadius")) { //MinRadius
                    minRad = getInt(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("Price")) { //MinRadius
                    price = getInt(hash3.getValue().toString());
                }
            }
            //Main.getInstance().getLogger().info("World MaxRad '" + world.getKey() + "' is " + maxRad);
            //Main.getInstance().getLogger().info("World MinRad '" + world.getKey() + "' is " + minRad);
            valid = this.name != null && (minRad != -1 || maxRad != -1);
        }

        private int getInt(String input) {
            return Integer.parseInt(input);
        }

        boolean isValid() {
            return valid;
        }
    }
}
