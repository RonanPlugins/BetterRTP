package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdQueue implements RTPCommand {

    public String getName() {
        return "queue";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        //sendi.sendMessage("Loading...");
        World world = args.length > 1 ? Bukkit.getWorld(args[1]) : null;
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            if (world != null) {
                sendInfo(sendi, queueGetWorld(p, world));
            } else
                queueWorlds(p);
        });
    }

    //World
    public static void sendInfo(CommandSender sendi, List<String> list) { //Send info
        list.add(0, "&e&m-----&6 BetterRTP &8| Queue &e&m-----");
        list.forEach(str ->
                list.set(list.indexOf(str), Message.color(str)));
        sendi.sendMessage(list.toArray(new String[0]));
    }

    private void queueWorlds(Player p) { //All worlds
        List<String> info = new ArrayList<>();
        for (World w : Bukkit.getWorlds())
            info.addAll(queueGetWorld(p, w));
        info.add("&eTotal of &a%amount% &egenerated locations".replace("%amount%", String.valueOf(DatabaseHandler.getQueue().getCount())));
        sendInfo(p, info);
    }

    private static List<String> queueGetWorld(Player player, World world) { //Specific world
        List<String> info = new ArrayList<>();
        info.add("&eWorld: &6" + world.getName());
        RTPSetupInformation setup_info = new RTPSetupInformation(HelperRTP.getActualWorld(player, world), player, player, true);
        WorldPlayer pWorld = HelperRTP.getPlayerWorld(setup_info);
        for (QueueData queue : QueueHandler.getApplicableAsync(pWorld)) {
            String str = "&8- &7x= &b%x, &7z= &b%z";
            Location loc = queue.getLocation();
            str = str.replace("%x", String.valueOf(loc.getBlockX())).replace("%z", String.valueOf(loc.getBlockZ()));
            info.add(str);
        }
        return info;
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> info = new ArrayList<>();
        if (args.length == 2) {
            for (World world : Bukkit.getWorlds())
                if (world.getName().startsWith(args[1]))
                    info.add(world.getName());
        }
        return info;
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNode.ADMIN.check(sendi);
    }
}
