package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_EditWorlds;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesUsage;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WORLD_TYPE;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdEdit implements RTPCommand, RTPCommandHelpable { //Edit a worlds properties

    public String getName() {
        return "edit";
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 4) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values()) {
                if (!cmd.name().equalsIgnoreCase(args[1])) continue;
                switch (cmd) {
                    case CUSTOMWORLD:
                        if (args.length >= 5) {
                            for (World world : Bukkit.getWorlds()) {
                                if (world.getName().equals(args[2])) {
                                    for (RTP_CMD_EDIT_SUB sub_cmd : RTP_CMD_EDIT_SUB.values())
                                        if (sub_cmd.name().equalsIgnoreCase(args[3])) {
                                            HelperRTP_EditWorlds.editCustomWorld(sendi, sub_cmd, world.getName(), args[4]);
                                            return;
                                        }
                                    usage(sendi, label, cmd);
                                    return;
                                }
                            }
                            MessagesCore.NOTEXIST.send(sendi, args[2]);
                            return;
                        }
                        usage(sendi, label, cmd);
                        return;
                    case LOCATION:
                        if (args.length >= 5) {
                            for (Map.Entry<String, RTPWorld> location : BetterRTP.getInstance().getRTP().getRTPworldLocations().entrySet()) {
                                if (location.getKey().equals(args[2])) {
                                    for (RTP_CMD_EDIT_SUB sub_cmd : RTP_CMD_EDIT_SUB.values())
                                        if (sub_cmd.name().equalsIgnoreCase(args[3])) {
                                            HelperRTP_EditWorlds.editLocation(sendi, sub_cmd, location.getKey(), args[4]);
                                            return;
                                        }
                                    usage(sendi, label, cmd);
                                    return;
                                }
                            }
                            usage(sendi, label, cmd);
                            return;
                        }
                        usage(sendi, label, cmd);
                        return;
                    case PERMISSION_GROUP:
                        if (BetterRTP.getInstance().getSettings().isPermissionGroupEnabled() && args.length >= 6) {
                            for (String group : BetterRTP.getInstance().getRTP().getPermissionGroups().keySet()) {
                                if (group.equals(args[2])) {
                                    for (World world : Bukkit.getWorlds()) {
                                        if (world.getName().equals(args[3])) {
                                            for (RTP_CMD_EDIT_SUB sub_cmd : RTP_CMD_EDIT_SUB.values())
                                                if (sub_cmd.name().toLowerCase().startsWith(args[4].toLowerCase())) {
                                                    HelperRTP_EditWorlds.editPermissionGroup(sendi, sub_cmd, group, world.getName(), args[5]);
                                                    return;
                                                }
                                            usage(sendi, label, cmd);
                                            return;
                                        }
                                        MessagesCore.NOTEXIST.send(sendi, args[3]);
                                        return;
                                    }
                                }
                            }
                        }
                        usage(sendi, label, cmd);
                        return;
                    case DEFAULT:
                        for (RTP_CMD_EDIT_SUB sub_cmd : RTP_CMD_EDIT_SUB.values())
                            if (sub_cmd.name().equalsIgnoreCase(args[2].toLowerCase())) {
                                HelperRTP_EditWorlds.editDefault(sendi, sub_cmd, args[3]);
                                return;
                            }
                        usage(sendi, label, cmd);
                        return;
                    case WORLD_TYPE:
                        for (World world : Bukkit.getWorlds()) {
                            if (world.getName().equals(args[2])) {
                                HelperRTP_EditWorlds.editWorldtype(sendi, args[2], args[3]);
                                //usage(sendi, label, cmd);
                                return;
                            }
                        }
                        MessagesCore.NOTEXIST.send(sendi, args[2]);
                        return;
                    case OVERRIDE:
                        for (World world : Bukkit.getWorlds()) {
                            if (world.getName().equals(args[2])) {
                                HelperRTP_EditWorlds.editOverride(sendi, args[2], args[3]);
                                //usage(sendi, label, cmd);
                                return;
                            }
                        }
                        MessagesCore.NOTEXIST.send(sendi, args[2]);
                        return;
                    case BLACKLISTEDBLOCKS:
                        if (args[2].equalsIgnoreCase("add")) {
                            HelperRTP_EditWorlds.editBlacklisted(sendi, args[3], true);
                        } else if (args[2].equalsIgnoreCase("remove")) {
                            HelperRTP_EditWorlds.editBlacklisted(sendi, args[3], false);
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
                        case PERMISSION_GROUP:
                            for (String group : BetterRTP.getInstance().getRTP().getPermissionGroups().keySet())
                                if (group.toLowerCase().startsWith(args[2].toLowerCase()))
                                    list.add(group);
                            break;
                        case LOCATION:
                            for (String location : BetterRTP.getInstance().getRTP().getRTPworldLocations().keySet())
                                if (location.toLowerCase().startsWith(args[2].toLowerCase()))
                                    list.add(location);
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
                        case LOCATION:
                            list.addAll(tabCompleteSub(args, cmd)); break;
                        case PERMISSION_GROUP:
                            for (World world : Bukkit.getWorlds())
                                if (world.getName().toLowerCase().startsWith(args[3].toLowerCase()))
                                    list.add(world.getName());
                            break;
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
                    switch (cmd) {
                        case CUSTOMWORLD:
                        case LOCATION:
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
                            break;
                        case PERMISSION_GROUP:
                            list.addAll(tabCompleteSub(args, cmd)); break;
                    }
        } else if (args.length == 6) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().equalsIgnoreCase(args[1]))
                    switch (cmd) {
                        case PERMISSION_GROUP:
                            if (args[4].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_X.name()))
                                list.add(String.valueOf(((Player) sendi).getLocation().getBlockX()));
                            else if (args[4].equalsIgnoreCase(RTP_CMD_EDIT_SUB.CENTER_Z.name()))
                                list.add(String.valueOf(((Player) sendi).getLocation().getBlockZ()));
                            else if (args[4].equalsIgnoreCase(RTP_CMD_EDIT_SUB.SHAPE.name()))
                                for (RTP_SHAPE shape : RTP_SHAPE.values())
                                    list.add(shape.name().toLowerCase());
                            break;
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
        return PermissionNode.EDIT.check(sendi);
    }

    private void usage(CommandSender sendi, String label, RTP_CMD_EDIT type) {
        if (type != null)
            switch (type) {
                case DEFAULT:
                    MessagesUsage.EDIT_DEFAULT.send(sendi, label); break;
                case CUSTOMWORLD:
                    MessagesUsage.EDIT_WORLD.send(sendi, label); break;
                case WORLD_TYPE:
                    MessagesUsage.EDIT_WORLDTYPE.send(sendi, label); break;
                case OVERRIDE:
                    MessagesUsage.EDIT_OVERRIDE.send(sendi, label); break;
                case BLACKLISTEDBLOCKS:
                    MessagesUsage.EDIT_BLACKLISTEDBLLOCKS.send(sendi, label); break;
                case PERMISSION_GROUP:
                    MessagesUsage.EDIT_PERMISSIONGROUP.send(sendi, label); break;
                case LOCATION:
                    MessagesUsage.EDIT_LOCATION.send(sendi, label); break;
            }
        else
            MessagesUsage.EDIT_BASE.send(sendi, label);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.EDIT.get();
    }

    public enum RTP_CMD_EDIT {
        //Mapped
        CUSTOMWORLD,
        PERMISSION_GROUP,
        LOCATION,
        //Custom Coded
        DEFAULT,
        WORLD_TYPE,
        OVERRIDE,
        BLACKLISTEDBLOCKS
    }

    public enum RTP_CMD_EDIT_SUB { //Only for World and Default
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

        public String get() {
            return str;
        }

        public Object getResult(String input) {
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