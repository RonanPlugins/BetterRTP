package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.CommandTypes;
import me.SuperRonanCraft.BetterRTP.player.commands.Commands;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdPlayer implements RTPCommand {

    //rtp player <world> <biome1> <biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length == 2)
            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline())
                getCmd().tp(Bukkit.getPlayer(args[1]), sendi, Bukkit.getPlayer(args[1]).getWorld().getName(), null);
            else if (Bukkit.getPlayer(args[1]) != null)
                getCmd().playerNotOnline(sendi, args[1]);
            else
                usage(sendi, label);
        else if (args.length >= 3)
            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline())
                getCmd().tp(Bukkit.getPlayer(args[1]), sendi, Bukkit.getWorld(args[2]).getName(), getCmd().getBiomes(args, 3, sendi));
            else if (Bukkit.getPlayer(args[1]) != null)
                getCmd().playerNotOnline(sendi, args[1]);
            else
                usage(sendi, label);
        else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(p.getDisplayName());
        } else if (args.length == 3) {
            for (World w : Bukkit.getWorlds())
                if (w.getName().startsWith(args[2]) && !Main.getInstance().getRTP().disabledWorlds().contains(w.getName()))
                    list.add(w.getName());
        } else if (args.length > 3) {
            if (CommandTypes.BIOME.getCmd().permission(sendi))
                getCmd().addBiomes(list, args);
        }
        return list;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getRtpOther(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        Main.getInstance().getText().getUsageRTPOther(sendi, label);
    }

    private Commands getCmd() {
        return Main.getInstance().getCmd();
    }
}
