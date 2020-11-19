package me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.cmds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.AddonMagicStick;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MagicStickCommand implements RTPCommand, RTPCommandHelpable {

    AddonMagicStick pl;

    public MagicStickCommand(AddonMagicStick pl) {
        this.pl = pl;
    }

    public String getName() {
        return "magicstick";
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
        sendi.sendMessage("Invalid argument!");
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2)
            for (subCmd subCmd : subCmd.values()) {
                if (subCmd.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(subCmd.name().toLowerCase());
                }
            }
        return list;
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
        GIVE(new MagicStickCommand_Give());

        MagicStickCommands cmd;

        subCmd(MagicStickCommands cmd) {
            this.cmd = cmd;
        }
    }
}
