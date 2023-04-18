package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CmdVersion implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "version";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Message_RTP.sms(sendi, "&aVersion #&e" + BetterRTP.getInstance().getDescription().getVersion());
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @NotNull public PermissionNode permission() {
        return PermissionNode.VERSION;
    }

    @Override
    public String getHelp() {
        return MessagesHelp.VERSION.get();
    }
}
