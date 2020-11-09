package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class CmdEdit implements RTPCommand, RTPCommandHelpable { //Edit a worlds properties

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 4) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    switch (cmd) {
                        case WORLD:
                            if (args.length >= 5) {
                                for (World world : Bukkit.getWorlds()) {
                                    if (world.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                        for (RTP_CMD_EDIT_SUB cmde : RTP_CMD_EDIT_SUB.values())
                                            if (cmde.name().toLowerCase().startsWith(args[3].toLowerCase())) {
                                                editWorld(sendi, cmde, args[4], args[2]);
                                                return;
                                            }
                                        usage(sendi, label, cmd);
                                        return;
                                    }
                                }
                                Main.getInstance().getText().getNotExist(sendi, label);
                                return;
                            }
                            usage(sendi, label, cmd);
                            return;
                        case DEFAULT:
                            for (RTP_CMD_EDIT_SUB cmde : RTP_CMD_EDIT_SUB.values())
                                if (cmde.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    editDefault(sendi, cmde, args[3]);
                                    return;
                                }
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

    private void editWorld(CommandSender sendi, RTP_CMD_EDIT_SUB cmd, String val, String world) {
        Object value;
        try {
            value = cmd.getResult(val);
        } catch (Exception e) {
            e.printStackTrace();
            Main.getInstance().getText().getEditError(sendi);
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

                    Main.getInstance().getText().getEditSet(sendi, cmd.get(), val);
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
            Main.getInstance().getRTP().loadWorldSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editDefault(CommandSender sendi, RTP_CMD_EDIT_SUB cmd, String val) {
        Object value = val;
        try {
            value = cmd.getResult(val);
        } catch (Exception e) {
            e.printStackTrace();
            Main.getInstance().getText().getEditError(sendi);
            return;
        }

        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();

        config.set("Default." + cmd.get(), value);

        try {
            config.save(file.getFile());
            Main.getInstance().getRTP().loadWorldSettings();
            Main.getInstance().getText().getEditSet(sendi, cmd.get(), val);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //rtp edit default <max/min/center/useworldborder> <value>
    //rtp edit world [<world>] <max/min/center/useworldborder> <value>
    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(cmd.name().toLowerCase());
        } else if (args.length == 3) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().equalsIgnoreCase(args[1])) {
                    switch (cmd) {
                        case WORLD: //List all worlds
                            for (World world : Bukkit.getWorlds())
                                if (world.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                                    list.add(world.getName());
                            break;
                        case DEFAULT:
                            list.addAll(tabCompleteSub(args));
                    }
                }
        } else if (args.length == 4) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().equalsIgnoreCase(args[1]))
                    switch (cmd) {
                        case WORLD:
                            list.addAll(tabCompleteSub(args)); break;
                        case DEFAULT:
                            if (args[2].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_X.name()))
                                list.add(String.valueOf(((Player) sendi).getLocation().getBlockX()));
                            else if (args[2].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_Z.name()))
                                list.add(String.valueOf(((Player) sendi).getLocation().getBlockZ()));
                            break;
                    }
        } else if (args.length == 5) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().equalsIgnoreCase(args[1]))
                    if (cmd == RTP_CMD_EDIT.WORLD) {
                        if (args[3].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_X.name()))
                            list.add(String.valueOf(((Player) sendi).getLocation().getBlockX()));
                        else if (args[3].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_Z.name()))
                            list.add(String.valueOf(((Player) sendi).getLocation().getBlockZ()));
                    }
        }
        return list;
    }

    private List<String> tabCompleteSub(String[] args) {
        List<String> list = new ArrayList<>();
        for (RTP_CMD_EDIT_SUB cmd : RTP_CMD_EDIT_SUB.values()) {
            if (cmd.name().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(cmd.name().toLowerCase());
        }
        return list;
    }

    @Override
    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getEdit(sendi);
    }

    private void usage(CommandSender sendi, String label, RTP_CMD_EDIT type) {
        if (type != null)
            switch (type) {
                case DEFAULT:
                    Main.getInstance().getText().getUsageEditDefault(sendi, label); break;
                case WORLD:
                    Main.getInstance().getText().getUsageEditWorld(sendi, label); break;
            }
        else
            Main.getInstance().getText().getUsageEdit(sendi, label);
    }

    @Override
    public String getHelp() {
        return Main.getInstance().getText().getHelpEdit();
    }

    enum RTP_CMD_EDIT {
        WORLD, DEFAULT
    }

    enum RTP_CMD_EDIT_SUB {
        CENTER_X("CenterX", "INT"),
        CENTER_Z("CenterZ", "INT"),
        MAX("MaxRadius", "INT"),
        MIN("MinRadius", "INT"),
        USEWORLDBORDER("UseWorldBorder", "BOL");

        private String type;
        private String str;

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
            return null;
        }
    }
}