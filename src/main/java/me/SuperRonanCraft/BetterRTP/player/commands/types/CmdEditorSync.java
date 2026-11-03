package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import java.util.Collections;
import java.util.List;

public class CmdEditorSync implements RTPCommand, RTPCommandHelpable {
    public String getName() { return "editorsync"; }
    public PermissionNode permission() { return PermissionNode.EDITOR; }
    public List<String> tabComplete(CommandSender sender, String[] args) { return Collections.emptyList(); }
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 2) BetterRTP.getInstance().getEditorBridge().redeem(sender, args[1]);
        else sender.sendMessage("Usage: /" + label + " editorsync <token>");
    }
    public String getHelp() { return MessagesHelp.EDITOR_SYNC.get(); }
}
