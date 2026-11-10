package me.SuperRonanCraft.BetterRTP.player.commands.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Info;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesUsage;

public class CmdWorld implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "world";
    }

    //rtp world <world> <biome1, biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2) {
            World world = Bukkit.getWorld(args[1]);
            if (world == null) //Check if world has spaces instead of underscores
                world = Bukkit.getWorld(args[1].replace("_", " "));
            if (world != null)
                CmdTeleport.teleport(sendi, label, world, HelperRTP_Info.getBiomes(args, 2, sendi));
            else
                MessagesCore.NOTEXIST.send(sendi, args[1]);
        } else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (World w : Bukkit.getWorlds()) {
                String _wName = w.getName().replace(" ", "_");
                if (w.getName().startsWith(args[1]) && !BetterRTP.getInstance().getRTP().getDisabledWorlds().contains(_wName)
                        && PermissionCheck.getAWorld(sendi, _wName))
                    list.add(_wName);
            }
        } else if (args.length >= 3) {
            if (PermissionNode.BIOME.check(sendi))
                HelperRTP_Info.addBiomes(list, args);
        }
        return list;
    }

    @NotNull public PermissionNode permission() {
        return PermissionNode.WORLD;
    }

    public void usage(CommandSender sendi, String label) {
        MessagesUsage.WORLD.send(sendi, label);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.WORLD.get();
    }
}
