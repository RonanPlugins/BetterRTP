package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandType;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RTP_CommandEvent extends Event {

    CommandSender sendi;
    RTPCommandType cmd;
    private static final HandlerList handler = new HandlerList();

    public RTP_CommandEvent(CommandSender sendi, RTPCommandType cmd) {
        this.sendi = sendi;
        this.cmd = cmd;
    }

    public CommandSender getSendi() {
        return sendi;
    }

    public RTPCommandType getCmd() {
        return cmd;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }
}
