package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandType;
import me.SuperRonanCraft.BetterRTP.player.commands.Commands;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Info;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdPlayer implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "player";
    }

    //rtp player <world> <biome1> <biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length == 2)
            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline())
                HelperRTP.tp(Bukkit.getPlayer(args[1]), sendi, Bukkit.getPlayer(args[1]).getWorld(), null, RTP_TYPE.FORCED);
            else if (Bukkit.getPlayer(args[1]) != null)
                BetterRTP.getInstance().getText().getNotOnline(sendi, args[1]);
            else
                usage(sendi, label);
        else if (args.length >= 3)
            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                World world = Bukkit.getWorld(args[2]);
                if (world != null) {
                    HelperRTP.tp(Bukkit.getPlayer(args[1]), sendi, world, HelperRTP_Info.getBiomes(args, 3, sendi), RTP_TYPE.FORCED);
                } else
                    BetterRTP.getInstance().getText().getNotExist(sendi, args[2]);
            } else if (Bukkit.getPlayer(args[1]) != null)
                BetterRTP.getInstance().getText().getNotOnline(sendi, args[1]);
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
                    list.add(p.getName());
        } else if (args.length == 3) {
            for (World w : Bukkit.getWorlds())
                if (w.getName().startsWith(args[2]) && !BetterRTP.getInstance().getRTP().getDisabledWorlds().contains(w.getName()))
                    list.add(w.getName());
        } else if (args.length > 3) {
            if (RTPCommandType.BIOME.getCmd().permission(sendi))
                HelperRTP_Info.addBiomes(list, args);
        }
        return list;
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNode.RTP_OTHER.check(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        BetterRTP.getInstance().getText().getUsageRTPOther(sendi, label);
    }

    private Commands getCmd() {
        return BetterRTP.getInstance().getCmd();
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpPlayer();
    }
}
