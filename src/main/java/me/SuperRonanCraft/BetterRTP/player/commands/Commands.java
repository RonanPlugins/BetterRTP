package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdTeleport;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownHandler;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CommandEvent;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

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
        if (pl.getPerms().getUse(sendi)) {
            if (args != null && args.length > 0) {
                for (RTPCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(args[0])) {
                        if (cmd.permission(sendi)) {
                            RTP_CommandEvent event = new RTP_CommandEvent(sendi, cmd);
                            //Command Event
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled())
                                cmd.execute(sendi, label, args);
                        } else
                            pl.getText().getNoPermission(sendi);
                        return;
                    }
                }
                pl.getText().getInvalid(sendi, label);
            } else {
                RTP_CommandEvent event = new RTP_CommandEvent(sendi, new CmdTeleport());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled())
                    event.getCmd().execute(sendi, label, args);
            }
        } else
            pl.getText().getNoPermission(sendi);
    }

    public List<String> onTabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (RTPCommand cmd : commands) {
                if (cmd.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    if (cmd.permission(sendi))
                        list.add(cmd.getName().toLowerCase());
            }
        } else if (args.length > 1) {
            for (RTPCommand cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(args[0]))
                    if (cmd.permission(sendi)) {
                        List<String> _cmdlist = cmd.tabComplete(sendi, args);
                        if (_cmdlist != null)
                            list.addAll(_cmdlist);
                    }
            }
        }
        return list;
    }
}
