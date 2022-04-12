package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTP_SETUP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPParticles;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldDefault;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import xyz.xenondevs.particle.ParticleEffect;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CmdQueue implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "queue";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length > 1 && Bukkit.getWorld(args[1]) != null) {
            sendInfo(sendi, queueGetWorld(Bukkit.getWorld(args[1])));
        } else
            queueWorlds(sendi);
    }

    @Override
    public String getHelp() {
        return null;
    }

    //World
    public static void sendInfo(CommandSender sendi, List<String> list) { //Send info
        list.add(0, "&e&m-----&6 BetterRTP &8| Queue &e&m-----");
        list.forEach(str ->
                list.set(list.indexOf(str), BetterRTP.getInstance().getText().color(str)));
        sendi.sendMessage(list.toArray(new String[0]));
    }

    private void queueWorlds(CommandSender sendi) { //All worlds
        List<String> info = new ArrayList<>();
        for (World w : Bukkit.getWorlds())
            info.addAll(queueGetWorld(w));
        info.add("&eTotal of &a%amount% generated locations".replace("%amount%", String.valueOf(QueueHandler.getCount())));
        sendInfo(sendi, info);
    }

    public static List<String> queueGetWorld(World world) { //Specific world
        List<String> info = new ArrayList<>();
        info.add("&eWorld: &6" + world.getName());
        for (QueueData queue : QueueHandler.getApplicable(world)) {
            String str = "&7x= &b%x, &7z= &b%z";
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
