package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTPAddons.PermissionNodeAddon;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PortalsCommand implements RTPCommand, RTPCommandHelpable {

    AddonPortals pl;

    public PortalsCommand(AddonPortals pl) {
        this.pl = pl;
    }

    public String getName() {
        return "portals";
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length > 1)
            for (subCmd subCmd : subCmd.values()) {
                if (args[1].equalsIgnoreCase(subCmd.name())) {
                    subCmd.cmd.execute(sendi, label, args, pl);
                    return;
                }
            }
        pl.msgs.usageBase(sendi, label);
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (subCmd cmd : subCmd.values()) {
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(cmd.name().toLowerCase());
                }
            }
        } else if (args.length >= 3) {
            for (subCmd cmd : subCmd.values()) {
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    if (cmd.cmd instanceof PortalsCommandsTabable)
                        list.addAll(((PortalsCommandsTabable) cmd.cmd).tabComplete(sendi, args, pl));
                }
            }
        }
        return list;
    }

    @Override
    public PermissionCheck permission() {
        return PermissionNodeAddon.PORTALS;
    }

    @Override
    public String getHelp() {
        return pl.msgs.getHelp();
    }

    private enum subCmd {
        LOC1(new PortalsCommand_Loc1()),
        LOC2(new PortalsCommand_Loc2()),
        CREATE(new PortalsCommand_Create()),
        REMOVE(new PortalsCommand_Remove()),
        LIST(new PortalsCommand_List());

        PortalsCommands cmd;

        subCmd(PortalsCommands cmd) {
            this.cmd = cmd;
        }
    }
}
