package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import org.bukkit.command.CommandSender;
import java.util.Collections;
import java.util.List;

public class CmdEditor implements RTPCommand, RTPCommandHelpable {
    public String getName() { return "editor"; }
    public PermissionNode permission() { return PermissionNode.EDITOR; }
    public String getHelp() { return "&7Open a private web config editor: &f/rtp editor &7(or cancel)"; }
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 2 && "cancel".startsWith(args[1].toLowerCase()) ? Collections.singletonList("cancel") : Collections.emptyList();
    }
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) BetterRTP.getInstance().getEditorBridge().open(sender);
        else if (args.length == 2 && "cancel".equalsIgnoreCase(args[1])) BetterRTP.getInstance().getEditorBridge().cancel(sender);
        else sender.sendMessage("Usage: /" + label + " editor [cancel]");
    }
}
