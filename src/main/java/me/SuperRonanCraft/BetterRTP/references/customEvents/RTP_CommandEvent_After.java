package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RTP_CommandEvent_After extends RTP_CommandEvent {

    //Executed after a command was executed
    public RTP_CommandEvent_After(CommandSender sendi, RTPCommand cmd) {
        super(sendi, cmd);
    }
}
