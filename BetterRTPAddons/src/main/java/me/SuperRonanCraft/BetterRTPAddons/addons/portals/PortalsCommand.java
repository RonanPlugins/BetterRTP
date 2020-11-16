package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds.*;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PortalsCommand implements RTPCommand, RTPCommandHelpable {
    AddonPortals pl;

    PortalsCommand(AddonPortals pl) {
        this.pl = pl;
    }

    public String getName() {
        return "portals";
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        for (subCmd subCmd : subCmd.values()) {
            if (args[1].equalsIgnoreCase(subCmd.name())) {
                subCmd.cmd.execute(sendi, label, args);
                return;
            }
        }
        sendi.sendMessage("Invalid command!");
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @Override
    public boolean permission(CommandSender sendi) {
        return BetterRTP.getInstance().getPerms().checkPerm("betterrtp.addon.portals", sendi);
    }

    @Override
    public String getHelp() {
        return pl.msgs.getHelp();
    }

    private enum subCmd {
        LOC1(new PortalsCommand_Loc1()),
        LOC2(new PortalsCommand_Loc2()),
        CREATE(new PortalsCommand_Create());

        PortalsCommands cmd;

        subCmd(PortalsCommands cmd) {
            this.cmd = cmd;
        }
    }
}
