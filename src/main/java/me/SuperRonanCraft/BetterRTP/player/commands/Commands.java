package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdTeleport;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CommandEvent_After;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    private final BetterRTP pl;
    public List<RTPCommand> commands = new ArrayList<>();

    public Commands(BetterRTP pl) {
        this.pl = pl;
    }

    public void load() {
        commands.clear();
        for (RTPCommandType cmd : RTPCommandType.values())
           registerCommand(cmd.getCmd(), false);
    }

    public void registerCommand(RTPCommand cmd, boolean forced) {
        if (!cmd.isDebugOnly() || pl.getSettings().isDebug() || forced) //If debug only, can it be enabled?
            commands.add(cmd);
    }

    public void commandExecuted(CommandSender sendi, String label, String[] args) {
        if (PermissionNode.USE.check(sendi)) {
            if (args != null && args.length > 0) {
                for (RTPCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(args[0])) {
                        if (cmd.permission().check(sendi)) {
                            RTP_CommandEvent event = new RTP_CommandEvent(sendi, cmd);
                            //Command Event
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                BetterRTP.debug(sendi.getName() + " executed: /" + label + " " + String.join(" ", args));
                                cmd.execute(sendi, label, args);
                                Bukkit.getServer().getPluginManager().callEvent(new RTP_CommandEvent_After(sendi, cmd));
                            }
                        } else
                            MessagesCore.NOPERMISSION.send(sendi, cmd);
                        return;
                    }
                }
                MessagesCore.INVALID.send(sendi, label);
            } else {
                RTP_CommandEvent event = new RTP_CommandEvent(sendi, new CmdTeleport());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled())
                    event.getCmd().execute(sendi, label, args);
            }
        } else
            MessagesCore.NOPERMISSION.send(sendi, PermissionNode.USE);
    }

    public List<String> onTabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (RTPCommand cmd : commands) {
                if (cmd.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    if (cmd.permission().check(sendi))
                        list.add(cmd.getName().toLowerCase());
            }
        } else if (args.length > 1) {
            for (RTPCommand cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(args[0]))
                    if (cmd.permission().check(sendi)) {
                        List<String> _cmdlist = cmd.tabComplete(sendi, args);
                        if (_cmdlist != null)
                            list.addAll(_cmdlist);
                    }
            }
        }
        return list;
    }
}
