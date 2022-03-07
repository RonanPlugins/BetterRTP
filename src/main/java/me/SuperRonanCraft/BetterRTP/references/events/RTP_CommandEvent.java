package me.SuperRonanCraft.BetterRTP.references.events;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class RTP_CommandEvent extends RTPEvent {

    CommandSender sendi;
    RTPCommand cmd;
    private static final HandlerList handler = new HandlerList();

    public RTP_CommandEvent(CommandSender sendi, RTPCommand cmd) {
        this.sendi = sendi;
        this.cmd = cmd;
    }

    public CommandSender getSendi() {
        return sendi;
    }

    public RTPCommand getCmd() {
        return cmd;
    }
}
