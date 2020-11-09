package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

@SuppressWarnings("rawtypes")
public class RTPPermissionGroup {
    private final List<RTPPermConfiguration> groups = new ArrayList<>();

    public RTPPermConfiguration getGroup(CommandSender p) {
        for (RTPPermConfiguration group : groups)
            if (Main.getInstance().getPerms().getPermissionGroup(p, group.name))
                return group;
        return null;
    }

    public void load() {
        for (RTPPermConfiguration group : groups)
            group.worlds.clear();
        groups.clear();
        YamlConfiguration config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG).getConfig();
        if (!config.getBoolean("PermissionGroup.Enabled")) return;
        List<Map<?, ?>> list = config.getMapList("PermissionGroup.Groups");
        for (Map<?, ?> m : list)
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                RTPPermConfiguration group = new RTPPermConfiguration(entry);
                if (group.isValid()) {
                    groups.add(group);
                    Main.debug("- Group " + group.name + " has " + group.worlds.size() + " worlds setup, permission: 'betterrtp.group." + group.name + "'");
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
        public String name;
        public List<RTPPermConfigurationWorld> worlds = new ArrayList<>();

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

        boolean valid;

        public int maxRad = -1;
        public int minRad = -1;
        public int price = -1;
        public int centerx = -1;
        public int centerz = -1;
        public Object useworldborder = null;

        public String name;

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
                } else if (field.equalsIgnoreCase("UseWorldBorder")) { //UseWorldBorder
                    useworldborder = Boolean.valueOf(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("CenterX")) { //Center X
                    centerx = getInt(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("CenterZ")) { //Center Z
                    centerz = getInt(hash3.getValue().toString());
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
