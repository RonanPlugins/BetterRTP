package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds.PortalsCommandsTabable;
import me.SuperRonanCraft.BetterRTPAddons.cmds.AddonsCommand_Help;
import me.SuperRonanCraft.BetterRTPAddons.cmds.AddonsCommand_List;
import me.SuperRonanCraft.BetterRTPAddons.cmds.AddonsCommand_Version;
import me.SuperRonanCraft.BetterRTPAddons.cmds.AddonsCommands;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddonsCommand implements RTPCommand, RTPCommandHelpable {

    public enum AddonCmds {
        LIST(new AddonsCommand_List()),
        HELP(new AddonsCommand_Help()),
        VERSION(new AddonsCommand_Version());

        public AddonsCommands cmd;

        AddonCmds(AddonsCommands cmd) {
            this.cmd = cmd;
        }
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2 && (args[1].equalsIgnoreCase("editor") || args[1].equalsIgnoreCase("editorsync"))) {
            Main plugin = Main.getInstance();
            if (!plugin.canEdit(sendi)) { sendi.sendMessage("§cYou need betterrtp.addon.editor permission."); return; }
            if (args[1].equalsIgnoreCase("editor") && args.length == 2) plugin.getEditorBridge().upload(sendi);
            else if (args[1].equalsIgnoreCase("editorsync") && args.length == 3) plugin.getEditorBridge().redeem(sendi, args[2]);
            else sendi.sendMessage("§eUsage: /" + label + " addons " + (args[1].equalsIgnoreCase("editor") ? "editor" : "editorsync <token>"));
            return;
        }
        if (args.length == 2) {
            for (AddonCmds cmds : AddonCmds.values()) {
                if (cmds.name().equalsIgnoreCase(args[1])) {
                    cmds.cmd.execute(sendi, label, args);
                    return;
                }
            }
            MessagesCore.INVALID.send(sendi, label);
        } else {
            List<String> list = new ArrayList<>();
            list.add(MessagesHelp.PREFIX.get());
            addEditorHelp(sendi, label, list);
            for (AddonsHandler.Addons addon : Main.getInstance().getAddonsHandler().addons) {
                RTPCommand cmd = addon.getCmd();
                if (cmd instanceof RTPCommandHelpable)
                    list.add(((RTPCommandHelpable) cmd).getHelp().replace("%command%", label));
            }
            list.add(((RTPCommandHelpable) AddonCmds.HELP.cmd).getHelp().replace("%command%", label));
            Main.getInstance().msgs.sms(sendi, list);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2 && Main.getInstance().canEdit(sendi)) {
            for (String command : new String[] { "editor", "editorsync" })
                if (command.startsWith(args[1].toLowerCase(Locale.ROOT))) list.add(command);
        }
        if (args.length == 2)
            for (AddonCmds cmds : AddonCmds.values()) {
                if (cmds.name().toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(cmds.name().toLowerCase());
            }
        return list;
    }

    public static void addEditorHelp(CommandSender sender, String label, List<String> list) {
        if (Main.getInstance().canEdit(sender)) {
            list.add("§7- §e/" + label + " addons editor §7- Open the Addons web editor");
            list.add("§7- §e/" + label + " addons editorsync <token> §7- Apply a saved Addons config");
        }
    }

    @Override
    public PermissionNode permission() {
        return PermissionNode.USE;
    }

    @Override
    public String getName() {
        return "addons";
    }

    @Override
    public String getHelp() {
        return Main.getInstance().msgs.getHelp();
    }
}
