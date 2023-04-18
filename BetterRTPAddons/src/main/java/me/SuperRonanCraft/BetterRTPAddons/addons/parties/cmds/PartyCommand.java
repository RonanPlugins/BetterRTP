package me.SuperRonanCraft.BetterRTPAddons.addons.parties.cmds;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTPAddons.PermissionNodeAddon;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.AddonParty;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PartyCommand implements RTPCommand, RTPCommandHelpable {

    AddonParty pl;

    public PartyCommand(AddonParty pl) {
        this.pl = pl;
    }

    @Override
    public String getName() {
        return "party";
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
                    if (cmd.cmd instanceof PartyCommandsTabable)
                        list.addAll(((PartyCommandsTabable) cmd.cmd).tabComplete(sendi, args, pl));
                }
            }
        }
        return list;
    }

    @Override
    public PermissionCheck permission() {
        return PermissionNodeAddon.PARTY;
    }

    @Override
    public String getHelp() {
        return pl.msgs.getHelp();
    }

    private enum subCmd {
        ACCEPT(new PartyCommand_Accept()),
        INVITE(new PartyCommand_Invite()),
        KICK(new PartyCommand_Kick()),
        LEAVE(new PartyCommand_Leave()),
        READYUP(new PartyCommand_ReadyUp()),
        ;

        PartyCommands cmd;

        subCmd(PartyCommands cmd) {
            this.cmd = cmd;
        }
    }
}
