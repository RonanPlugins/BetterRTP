package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.Commands;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdLocation implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "location";
    }

    //rtp location <location name> [player]
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length == 2) {
            if (sendi instanceof  Player) {
                for (String location_name : getLocations(sendi, null).keySet()) {
                    if (location_name.equalsIgnoreCase(args[1].toLowerCase())) {
                        Player p = (Player) sendi;
                        HelperRTP.tp(p, sendi, null, null, RTP_TYPE.COMMAND, false, false, (WorldLocations) getLocations().get(location_name));
                        return;
                    }
                }
                usage(sendi, label);
            } else
                sendi.sendMessage("Console is not able to execute this command! Try '/rtp help'");
        } else if (args.length == 3 && PermissionNode.RTP_OTHER.check(sendi)) {
            Player p = Bukkit.getPlayer(args[2]);
            if (p != null && p.isOnline()) {
                for (String location_name : getLocations(sendi, null).keySet()) {
                    if (location_name.equalsIgnoreCase(args[1].toLowerCase())) {
                        HelperRTP.tp(p, sendi, null, null, RTP_TYPE.COMMAND, false, false, (WorldLocations) getLocations().get(location_name));
                        return;
                    }
                }
                usage(sendi, label);
            } else if (p != null)
                BetterRTP.getInstance().getText().getNotOnline(sendi, args[1]);
            else
                usage(sendi, label);
        } else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (String location_name : getLocations(sendi, null).keySet())
                if (location_name.toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(location_name);
        } else if (args.length == 3 && PermissionNode.RTP_OTHER.check(sendi)) {
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    list.add(p.getName());
        }
        return list;
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNode.LOCATION.check(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        BetterRTP.getInstance().getText().getUsageLocation(sendi, label);
    }

    private static HashMap<String, RTPWorld> getLocations() {
        return BetterRTP.getInstance().getRTP().worldLocations;
    }

    //Get locations a player has access to
    public static HashMap<String, RTPWorld> getLocations(CommandSender sendi, @Nullable World world) {
        if (BetterRTP.getInstance().getSettings().isLocationNeedPermission()) {
            HashMap<String, RTPWorld> locations = new HashMap<>();
            for (Map.Entry<String, RTPWorld> location : getLocations().entrySet())
                if (PermissionNode.getLocation(sendi, location.getKey())) {
                    if (world == null || location.getValue().getWorld().equals(world))
                        locations.put(location.getKey(), location.getValue());
                }
            return locations;
        } else
            return getLocations();
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpLocation();
    }
}
