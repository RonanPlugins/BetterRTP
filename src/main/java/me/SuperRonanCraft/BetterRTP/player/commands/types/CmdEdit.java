package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.worlds.Custom;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdEdit implements RTPCommand { //Edit a worlds properties

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 4) {
            for (RTP_CMD_EDIT cmd : RTP_CMD_EDIT.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    switch (cmd) {
                        case WORLD:
                            if (args.length >= 5) {
                                for (World world : Bukkit.getWorlds())
                                    if (world.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                        for (RTP_CMD_EDIT_SUB cmde : RTP_CMD_EDIT_SUB.values())
                                            if (cmde.name().toLowerCase().startsWith(args[3].toLowerCase())) {
                                                editWorld(sendi, cmde, args[4], world);
                                                return;
                                            }
                                        usage(sendi, label);
                                        return;
                                    }
                                Main.getInstance().getText().getNotExist(sendi, label);
                            } else
                                usage(sendi, label);
                            break;
                        case DEFAULT:
                            for (RTP_CMD_EDIT_SUB cmde : RTP_CMD_EDIT_SUB.values())
                                if (cmde.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    editDefault(sendi, cmde, args[3]);
                                    return;
                                }
                            usage(sendi, label);
                            break;
                    }
                }
        } else
            usage(sendi, label);
    }

    private void editWorld(CommandSender sendi, RTP_CMD_EDIT_SUB cmd, String value, World world) {
        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();

        HashMap<String, List<String>> map = (HashMap<String, List<String>>) config.getMapList("CustomWorlds");

        if (map.containsKey())

        config.set("CustomWorlds", customWorlds);

        try {
            config.save(file.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editDefault(CommandSender sendi, RTP_CMD_EDIT_SUB cmd, String value) {
        FileBasics.FILETYPE file = FileBasics.FILETYPE.CONFIG;
        YamlConfiguration config = file.getConfig();



        try {
            config.save(file.getFile());
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
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
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
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase()))
                    if (cmd == RTP_CMD_EDIT.WORLD)
                        list.addAll(tabCompleteSub(args));
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

    private void usage(CommandSender sendi, String label) {
        Main.getInstance().getText().getUsageEdit(sendi, label);
    }

    enum RTP_CMD_EDIT {
        WORLD, DEFAULT
    }

    enum RTP_CMD_EDIT_SUB {
        CENTER, MAX, MIN, USEWORLDBORDER
    }
}