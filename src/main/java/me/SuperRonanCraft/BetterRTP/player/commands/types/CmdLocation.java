package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesUsage;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
            if (sendi instanceof Player) {
                Player p = (Player) sendi;
                for (Map.Entry<String, RTPWorld> location : getLocations(sendi, p.getWorld()).entrySet())
                    if (location.getKey().equalsIgnoreCase(args[1].toLowerCase())) {
                        HelperRTP.tp(p, sendi, null, null, RTP_TYPE.COMMAND, false, false, (WorldLocation) location.getValue());
                        return;
                    }
                usage(sendi, label);
            } else
                sendi.sendMessage("Console is not able to execute this command! Try '/rtp help'");
        } else if (args.length == 3 && PermissionNode.RTP_OTHER.check(sendi)) {
            Player p = Bukkit.getPlayer(args[2]);
            if (p != null && p.isOnline()) {
                for (Map.Entry<String, RTPWorld> location : getLocations(sendi, null).entrySet()) {
                    if (location.getKey().equalsIgnoreCase(args[1].toLowerCase())) {
                        HelperRTP.tp(p, sendi, null, null, RTP_TYPE.COMMAND, false, false, (WorldLocation) location.getValue());
                        return;
                    }
                }
                usage(sendi, label);
            } else if (p != null)
                MessagesCore.NOTONLINE.send(sendi, args[1]);
            else
                usage(sendi, label);
        } else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            Player p = (Player) sendi;
            for (String location_name : getLocations(sendi, p.getWorld()).keySet())
                if (location_name.toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(location_name);
        } else if (args.length == 3 && PermissionNode.RTP_OTHER.check(sendi)) {
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    list.add(p.getName());
        }
        return list;
    }

    @NotNull public PermissionNode permission() {
        return PermissionNode.LOCATION;
    }

    public void usage(CommandSender sendi, String label) {
        MessagesUsage.LOCATION.send(sendi, label);
    }

    //Get locations a player has access to
    public static HashMap<String, RTPWorld> getLocations(CommandSender sendi, @Nullable World world) {
        HashMap<String, RTPWorld> locations = new HashMap<>();
        boolean needPermission = BetterRTP.getInstance().getSettings().isLocationNeedPermission();
        boolean needSameWorld = BetterRTP.getInstance().getSettings().isUseLocationsInSameWorld();
        if (needSameWorld)
            needSameWorld = !PermissionNode.BYPASS_LOCATION.check(sendi);
        for (Map.Entry<String, RTPWorld> location : BetterRTP.getInstance().getRTP().getRTPworldLocations().entrySet()) {
            boolean add = true;
            if (needPermission) //Do we need permission to go to this location?
                add = PermissionCheck.getLocation(sendi, location.getKey());
            if (add && needSameWorld) //Can be added and needs same world (if not same world, we don't check)
                add = world == null || location.getValue().getWorld().equals(world);
            if (add) //Location can be added to list
                locations.put(location.getKey(), location.getValue());
        }
        return locations;
    }

    @Override
    public String getHelp() {
        return MessagesHelp.LOCATION.get();
    }

    @Override public boolean enabled() {
        return BetterRTP.getInstance().getSettings().isLocationEnabled();
    }
}
