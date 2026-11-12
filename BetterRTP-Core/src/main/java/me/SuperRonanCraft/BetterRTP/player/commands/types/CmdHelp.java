package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdHelp implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "help";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        List<String> list = new ArrayList<>();
        list.add(MessagesHelp.PREFIX.get());
        list.add(MessagesHelp.MAIN.get());
        for (RTPCommand cmd : BetterRTP.getInstance().getCmd().commands)
            if (cmd.permission().check(sendi))
                if (cmd instanceof RTPCommandHelpable) {
                    String help = ((RTPCommandHelpable) cmd).getHelp();
                    list.add(help);
                }
        Message_RTP.sms(sendi, list, Collections.singletonList(label));
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }


    @NotNull public PermissionNode permission() {
        return PermissionNode.USE;
    }

    @Override
    public String getHelp() {
        return MessagesHelp.HELP.get();
    }
}
