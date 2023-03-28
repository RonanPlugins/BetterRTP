package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.references.web.LogUploader;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
                sendInfo(sendi, queueGetWorld(p, world), label, args);
            } else
                queueWorlds(p, label, args);
        });
    }

    //World
    public static void sendInfo(CommandSender sendi, List<String> list, String label, String[] args) { //Send info
        boolean upload = Arrays.asList(args).contains("_UPLOAD_");
        list.add(0, "&e&m-----&6 BetterRTP &8| Queue &e&m-----");
        list.forEach(str -> list.set(list.indexOf(str), Message.color(str)));
        String cmd = "/" + label + " " + String.join(" ", args);
        if (!upload) {
            sendi.sendMessage(list.toArray(new String[0]));
            if (sendi instanceof Player) {
                TextComponent component = new TextComponent(Message.color("&7- &7Click to upload command log to &flogs.ronanplugins.com"));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd + " _UPLOAD_"));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.color("&6Suggested command&f: &7" + "/betterrtp " + String.join(" ", args) + " _UPLOAD_")).create()));
                ((Player) sendi).spigot().sendMessage(component);
            } else {
                sendi.sendMessage("Execute `" + cmd + " _UPLOAD_`" + " to upload command log to https://logs.ronanplugins.com");
            }
        } else {
            list.add(0, "Command: " + cmd);
            list.forEach(str -> list.set(list.indexOf(str), ChatColor.stripColor(str)));
            CompletableFuture.runAsync(() -> {
                String key = LogUploader.post(list);
                if (key == null) {
                    Message.sms(sendi, new ArrayList<>(Collections.singletonList("&cAn error occured attempting to upload log!")), null);
                } else {
                    try {
                        JSONObject json = (JSONObject) new JSONParser().parse(key);
                        Message.sms(sendi, Arrays.asList(" ", Message.getPrefix(Message_RTP.msg) + "&aLog uploaded! &fView&7: &6https://logs.ronanplugins.com/" + json.get("key")), null);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    private void queueWorlds(Player p, String label, String[] args) { //All worlds
        List<String> info = new ArrayList<>();
        int locs = 0;
        for (World w : Bukkit.getWorlds()) {
            List<String> list = queueGetWorld(p, w);
            info.addAll(list);
            locs += list.size();
        }
        info.add("&eTotal of &a%amount% &egenerated locations".replace("%amount%", String.valueOf(locs)));
        sendInfo(p, info, label, args);
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
