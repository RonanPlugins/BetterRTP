package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.player.commands.Commands;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.CommandTypes;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdWorld implements RTPCommand, RTPCommandHelpable {

    //rtp world <world> <biome1, biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2)
            Main.getInstance().getCmd().rtp(sendi, label, args[1], Main.getInstance().getCmd().getBiomes(args, 2, sendi));
        else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (World w : Bukkit.getWorlds()) {
                String _wName = w.getName().replace(" ", "_");
                if (w.getName().startsWith(args[1]) && !Main.getInstance().getRTP().disabledWorlds().contains(_wName)
                        && Main.getInstance().getPerms().getAWorld(sendi, _wName))
                    list.add(_wName);
            }
        } else if (args.length >= 3) {
            if (CommandTypes.BIOME.getCmd().permission(sendi))
                getCmd().addBiomes(list, args);
        }
        return list;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getWorld(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        Main.getInstance().getText().getUsageWorld(sendi, label);
    }

    private Commands getCmd() {
        return Main.getInstance().getCmd();
    }

    @Override
    public String getHelp() {
        return Main.getInstance().getText().getHelpWorld();
    }
}
