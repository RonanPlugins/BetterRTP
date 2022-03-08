package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandType;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Info;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdWorld implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "world";
    }

    //rtp world <world> <biome1, biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2)
            HelperRTP.rtp(sendi, label, args[1], HelperRTP_Info.getBiomes(args, 2, sendi));
        else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (World w : Bukkit.getWorlds()) {
                String _wName = w.getName().replace(" ", "_");
                if (w.getName().startsWith(args[1]) && !BetterRTP.getInstance().getRTP().getDisabledWorlds().contains(_wName)
                        && BetterRTP.getInstance().getPerms().getAWorld(sendi, _wName))
                    list.add(_wName);
            }
        } else if (args.length >= 3) {
            if (RTPCommandType.BIOME.getCmd().permission(sendi))
                HelperRTP_Info.addBiomes(list, args);
        }
        return list;
    }

    public boolean permission(CommandSender sendi) {
        return BetterRTP.getInstance().getPerms().getWorld(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        BetterRTP.getInstance().getText().getUsageWorld(sendi, label);
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpWorld();
    }
}
