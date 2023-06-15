package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_PlayerInfo;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesUsage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CmdPlayerSudo implements RTPCommand {

    public String getName() {
        return "player_sudo";
    }

    //rtp sudoplayer <player> <world> <RTP_PlayerInfo.RTP_PLAYERINFO_FLAG...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length == 2)
            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                HelperRTP.tp(Bukkit.getPlayer(args[1]),
                        sendi,
                        Bukkit.getPlayer(args[1]).getWorld(),
                        null,
                        RTP_TYPE.FORCED,
                        null,
                        new RTP_PlayerInfo(false, true, false, false, false));
            } else if (Bukkit.getPlayer(args[1]) != null)
                MessagesCore.NOTONLINE.send(sendi, args[1]);
            else
                usage(sendi, label);
        else if (args.length >= 3)
            if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                World world = Bukkit.getWorld(args[2]);
                if (world != null) {
                    HelperRTP.tp(Bukkit.getPlayer(args[1]),
                            sendi,
                            world,
                            null,
                            RTP_TYPE.FORCED,
                            null,
                            new RTP_PlayerInfo(false, true, false, false, false));
                } else
                    MessagesCore.NOTEXIST.send(sendi, args[2]);
            } else if (Bukkit.getPlayer(args[1]) != null)
                MessagesCore.NOTONLINE.send(sendi, args[1]);
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
        }
        return list;
    }

    @NotNull public PermissionNode permission() {
        return PermissionNode.RTP_OTHER;
    }

    public void usage(CommandSender sendi, String label) {
        MessagesUsage.RTP_OTHER.send(sendi, label);
    }
}
