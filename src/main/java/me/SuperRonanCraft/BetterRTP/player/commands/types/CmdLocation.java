package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.Commands;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandType;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.worlds.WorldLocations;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmdLocation implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "location";
    }

    //rtp location <location name>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length == 2) {
            for (String location_name : getLocations().keySet()) {
                if (location_name.equalsIgnoreCase(args[1].toLowerCase())) {
                    //LOCATION RTP CODE
                    //**********************************
                    //**********************************
                    return;
                }
            }
            usage(sendi, label);
        } else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (String location_name : getLocations().keySet())
                if (location_name.toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(location_name);
        }
        return list;
    }

    public boolean permission(CommandSender sendi) {
        return BetterRTP.getInstance().getPerms().getRtpOther(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        BetterRTP.getInstance().getText().getUsageLocation(sendi, label);
    }

    private HashMap<String, RTPWorld> getLocations() {
        return BetterRTP.getInstance().getRTP().worldLocations;
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpLocation();
    }
}
