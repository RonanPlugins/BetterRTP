package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WORLD_TYPE;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class CmdEdit implements RTPCommand, RTPCommandHelpable { //Edit a worlds properties

    public String getName() {
        return "edit";
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 4) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    switch (cmd) {
                        case CUSTOMWORLD:
                            if (args.length >= 5) {
                                for (World world : Bukkit.getWorlds()) {
                                    if (world.getName().startsWith(args[2])) {
                                        for (RTP_CMD_EDIT_SUB sub_cmd : RTP_CMD_EDIT_SUB.values())
                                            if (sub_cmd.name().toLowerCase().startsWith(args[3].toLowerCase())) {
                                                editWorld(sendi, sub_cmd, args[2], args[4]);
                                                return;
                                            }
                                        usage(sendi, label, cmd);
                                        return;
                                    }
                                }
                                BetterRTP.getInstance().getText().getNotExist(sendi, args[2]);
                                return;
                            }
                            usage(sendi, label, cmd);
                            return;
                        case DEFAULT:
                            for (RTP_CMD_EDIT_SUB sub_cmd : RTP_CMD_EDIT_SUB.values())
                                if (sub_cmd.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    editDefault(sendi, sub_cmd, args[3]);
                                    return;
                                }
                            usage(sendi, label, cmd);
                            return;
                        case WORLD_TYPE:
                            for (World world : Bukkit.getWorlds()) {
                                if (world.getName().startsWith(args[2])) {
                                    editWorldtype(sendi, args[2], args[3]);
                                    //usage(sendi, label, cmd);
                                    return;
                                }
                            }
                            BetterRTP.getInstance().getText().getNotExist(sendi, args[2]);
                            return;
                        case OVERRIDE:
                            for (World world : Bukkit.getWorlds()) {
                                if (world.getName().startsWith(args[2])) {
                                    editOverride(sendi, args[2], args[3]);
                                    //usage(sendi, label, cmd);
                                    return;
                                }
                            }
                            BetterRTP.getInstance().getText().getNotExist(sendi, args[2]);
                            return;
                        case BLACKLISTEDBLOCKS:
                            if (args[2].equalsIgnoreCase("add")) {
                                editBlacklisted(sendi, args[3], true);
                            } else if (args[2].equalsIgnoreCase("remove")) {
                                editBlacklisted(sendi, args[3], false);
                            } else
                                usage(sendi, label, cmd);
                            return;
                    }
                }
        } else if (args.length >= 2) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    usage(sendi, label, cmd);
                    return;
                }
        }
            usage(sendi, label, null);
    }

    private void editLocation(CommandSender sendi, RTP_CMD_EDIT_SUB cmd, String location, String val) {
        Object value;
        try {
            value = cmd.getResult(val);
        } catch (Exception e) {
            e.printStackTrace();
            BetterRTP.getInstance().getText().getEditError(sendi);
            return;
        }

        if (value == null) {
            BetterRTP.getInstance().getText().getEditError(sendi);
            return;
        }

        FileBasics.FILETYPE file = FileBasics.FILETYPE.LOCATIONS;
        YamlConfiguration config = file.getConfig();

        List<Map<?, ?>> map = config.getMapList("Locations");
        boolean found = false;
        for (Map<?, ?> m : map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                if (entry.getKey().toString().equals(location)) {
                    found = true;
                    for (Object map2 : m.values()) {
                        Map<Object, Object> values = (Map<Object, Object>) map2;
                        values.put(cmd.get(), value);
                        BetterRTP.getInstance().getText().getEditSet(sendi, cmd.get(), val);
                    }
                    break;
                }
            }
        }

        if (found) {
            config.set("Locations", map);
            try {
                config.save(file.getFile());
                BetterRTP.getInstance().getRTP().loadLocations();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void editWorld(CommandSender sendi, RTP_CMD_EDIT_SUB cmd, String world, String val) {
        Object value;
        try {
            value = cmd.getResult(val);
        } catch (Exception e) {
            e.printStackTrace();
            BetterRTP.getInstance().getText().getEditError(sendi);
            return;
        }

        if (value == null) {
            BetterRTP.getInstance().getText().getEditError(sendi);
            return;
        }

        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();

        List<Map<?, ?>> map = config.getMapList("CustomWorlds");
        boolean found = false;
        for (Map<?, ?> m : map) {
            if (m.keySet().toArray()[0].equals(world)) {
                found = true;
                for (Object map2 : m.values()) {
                    Map<Object, Object> values = (Map<Object, Object>) map2;
                    values.put(cmd.get(), value);
                    BetterRTP.getInstance().getText().getEditSet(sendi, cmd.get(), val);
                }
                break;
            }
        }
        if (!found) {
            Map<Object, Object> map2 = new HashMap<>();
            Map<Object, Object> values = new HashMap<>();
            values.put(cmd.get(), value);
            map2.put(world, values);
            map.add(map2);
        }

        config.set("CustomWorlds", map);

        try {
            config.save(file.getFile());
            BetterRTP.getInstance().getRTP().loadWorlds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editDefault(CommandSender sendi, RTP_CMD_EDIT_SUB cmd, String val) {
        Object value;
        try {
            value = cmd.getResult(val);
        } catch (Exception e) {
            e.printStackTrace();
            BetterRTP.getInstance().getText().getEditError(sendi);
            return;
        }

        if (value == null) {
            BetterRTP.getInstance().getText().getEditError(sendi);
            return;
        }

        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();

        config.set("Default." + cmd.get(), value);

        try {
            config.save(file.getFile());
            BetterRTP.getInstance().getRTP().loadWorlds();
            BetterRTP.getInstance().getText().getEditSet(sendi, cmd.get(), val);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editWorldtype(CommandSender sendi, String world, String val) {
        //sendi.sendMessage("Editting worldtype for world " + world + " to " + val);
        WORLD_TYPE type;
        try {
            type = WORLD_TYPE.valueOf(val.toUpperCase());
        } catch (Exception e) {
            //e.printStackTrace();
            BetterRTP.getInstance().getText().getEditError(sendi);
            return;
        }

        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();

        List<Map<?, ?>> world_map = config.getMapList("WorldType");
        List<Map<?, ?>> removeList = new ArrayList<>();
        for (Map<?, ?> m : world_map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                if (entry.getKey().equals(world))
                    removeList.add(m);
            }
        }
        for (Map<?, ?> o : removeList)
            world_map.remove(o);
        Map<String, String> newIndex = new HashMap<>();
        newIndex.put(world, type.name());
        world_map.add(newIndex);
        config.set("WorldType", world_map);

        try {
            config.save(file.getFile());
            BetterRTP.getInstance().getRTP().load();
            BetterRTP.getInstance().getText().getEditSet(sendi, RTP_CMD_EDIT.WORLD_TYPE.name(), val);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editOverride(CommandSender sendi, String world, String val) {

        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();

        List<Map<?, ?>> world_map = config.getMapList("Overrides");
        List<Map<?, ?>> removeList = new ArrayList<>();
        for (Map<?, ?> m : world_map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                if (entry.getKey().equals(world))
                    removeList.add(m);
            }
        }
        for (Map<?, ?> o : removeList)
            world_map.remove(o);
        if (!val.equals("REMOVE_OVERRIDE")) {
            Map<String, String> newIndex = new HashMap<>();
            newIndex.put(world, val);
            world_map.add(newIndex);
        } else {
            val = "(removed override)";
        }
        config.set("Overrides", world_map);

        try {
            config.save(file.getFile());
            BetterRTP.getInstance().getRTP().load();
            BetterRTP.getInstance().getText().getEditSet(sendi, RTP_CMD_EDIT.OVERRIDE.name(), val);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editBlacklisted(CommandSender sendi, String block, boolean add) {

        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();

        List<String> world_map = config.getStringList("BlacklistedBlocks");
        List<String> removeList = new ArrayList<>();
        for (String m : world_map) {
            if (m.equals(block)) {
                removeList.add(m);
            }
        }
        for (String o : removeList)
            world_map.remove(o);
        if (add) {
            world_map.add(block);
        } else {
            block = "(removed " + block + ")";
        }
        config.set("BlacklistedBlocks", world_map);

        try {
            config.save(file.getFile());
            BetterRTP.getInstance().getRTP().load();
            BetterRTP.getInstance().getText().getEditSet(sendi, RTP_CMD_EDIT.BLACKLISTEDBLOCKS.name(), block);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //rtp edit default <max/min/center/useworldborder> <value>
    //rtp edit world [<world>] <max/min/center/useworldborder> <value>
    //rtp edit worldtype <world> <value>
    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(cmd.name().toLowerCase());
        } else if (args.length == 3) { //rtp edit <sub_cmd> <type>
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().equalsIgnoreCase(args[1])) {
                    switch (cmd) {
                        case WORLD_TYPE:
                        case OVERRIDE:
                        case CUSTOMWORLD: //List all worlds
                            for (World world : Bukkit.getWorlds())
                                if (world.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                                    list.add(world.getName());
                            break;
                        case DEFAULT:
                            list.addAll(tabCompleteSub(args, cmd));
                            break;
                        case BLACKLISTEDBLOCKS:
                            list.add("add");
                            list.add("remove");
                            break;
                    }
                }
        } else if (args.length == 4) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().equalsIgnoreCase(args[1]))
                    switch (cmd) {
                        case CUSTOMWORLD:
                            list.addAll(tabCompleteSub(args, cmd)); break;
                        case DEFAULT:
                            if (args[2].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_X.name()))
                                list.add(String.valueOf(((Player) sendi).getLocation().getBlockX()));
                            else if (args[2].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_Z.name()))
                                list.add(String.valueOf(((Player) sendi).getLocation().getBlockZ()));
                            break;
                        case WORLD_TYPE:
                            for (WORLD_TYPE _type : WORLD_TYPE.values())
                                list.add(_type.name());
                            break;
                        case OVERRIDE:
                            for (World world : Bukkit.getWorlds())
                                if (world.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                                    list.add(world.getName());
                            list.add("REMOVE_OVERRIDE");
                            break;
                        case BLACKLISTEDBLOCKS:
                            if (args[2].equalsIgnoreCase("add")) {
                                for (Material block : Material.values()) {
                                    if (list.size() > 20)
                                        break;
                                    if (block.name().startsWith(args[3].toUpperCase()))
                                        list.add(block.name());
                                }
                            } else if (args[2].equalsIgnoreCase("remove")) {
                                for (String block : BetterRTP.getInstance().getRTP().getBlockList()) {
                                    if (block.startsWith(args[3]))
                                        list.add(block);
                                }
                            }
                            break;
                    }
        } else if (args.length == 5) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().equalsIgnoreCase(args[1]))
                    if (cmd == RTP_CMD_EDIT.CUSTOMWORLD) {
                        if (args[3].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_X.name()))
                            list.add(String.valueOf(((Player) sendi).getLocation().getBlockX()));
                        else if (args[3].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_Z.name()))
                            list.add(String.valueOf(((Player) sendi).getLocation().getBlockZ()));
                        else if (args[3].equalsIgnoreCase(RTP_CMD_EDIT_SUB.SHAPE.name()))
                            for (RTP_SHAPE shape : RTP_SHAPE.values())
                                list.add(shape.name().toLowerCase());
                        /*else if (args[3].equalsIgnoreCase(RTP_CMD_EDIT_SUB.BIOME_ADD.name()))
                            for (Biome biome : Biome.values()) {
                                if (biome.name().toLowerCase().startsWith(args[4].toLowerCase()) && list.size() < 20)
                                    list.add(biome.name());
                            }*/
                    }
        }
        return list;
    }

    private List<String> tabCompleteSub(String[] args, RTP_CMD_EDIT cmd) {
        List<String> list = new ArrayList<>();
        for (RTP_CMD_EDIT_SUB sub_cmd : RTP_CMD_EDIT_SUB.values()) {

            if (sub_cmd.name().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(sub_cmd.name().toLowerCase());
        }
        return list;
    }

    @Override
    public boolean permission(CommandSender sendi) {
        return BetterRTP.getInstance().getPerms().getEdit(sendi);
    }

    private void usage(CommandSender sendi, String label, RTP_CMD_EDIT type) {
        if (type != null)
            switch (type) {
                case DEFAULT:
                    BetterRTP.getInstance().getText().getUsageEditDefault(sendi, label); break;
                case CUSTOMWORLD:
                    BetterRTP.getInstance().getText().getUsageEditWorld(sendi, label); break;
                case WORLD_TYPE:
                    BetterRTP.getInstance().getText().getUsageWorldtype(sendi, label); break;
                case OVERRIDE:
                    BetterRTP.getInstance().getText().getUsageOverride(sendi, label); break;
                case BLACKLISTEDBLOCKS:
                    BetterRTP.getInstance().getText().getUsageBlacklistedBlocks(sendi, label); break;
            }
        else
            BetterRTP.getInstance().getText().getUsageEdit(sendi, label);
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpEdit();
    }

    enum RTP_CMD_EDIT {
        CUSTOMWORLD, LOCATION, DEFAULT, WORLD_TYPE, OVERRIDE, BLACKLISTEDBLOCKS
    }

    enum RTP_CMD_EDIT_SUB { //Only for World and Default
        CENTER_X("CenterX", "INT"),
        CENTER_Z("CenterZ", "INT"),
        MAXRAD("MaxRadius", "INT"),
        MINRAD("MinRadius", "INT"),
        MAXY("MaxY", "INT"),
        MINY("MinY", "INT"),
        PRICE("Price", "INT"),
        SHAPE("Shape", "SHAPE"),
        //BIOME_ADD("Biomes", "STR"),
        USEWORLDBORDER("UseWorldBorder", "BOL");

        private final String type;
        private final String str;

        RTP_CMD_EDIT_SUB(String str, String type) {
            this.str = str;
            this.type = type;
        }

        String get() {
            return str;
        }

        Object getResult(String input) {
            if (this.type.equalsIgnoreCase("INT"))
                return Integer.parseInt(input);
            else if (this.type.equalsIgnoreCase("BOL"))
                return Boolean.valueOf(input);
            else if (this.type.equalsIgnoreCase("STR"))
                return Collections.singletonList(input);
            else if (this.type.equalsIgnoreCase("SHAPE")) {
                try {
                    return RTP_SHAPE.valueOf(input.toUpperCase()).name();
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }
}