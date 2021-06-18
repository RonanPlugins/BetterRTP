package me.SuperRonanCraft.BetterRTPAddons.cmds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTPAddons.AddonMessages;
import me.SuperRonanCraft.BetterRTPAddons.AddonsCommand;
import me.SuperRonanCraft.BetterRTPAddons.AddonsHandler;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AddonsCommand_Help implements AddonsCommands, RTPCommandHelpable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        List<String> list = new ArrayList<>();
        list.add(BetterRTP.getInstance().getText().getHelpPrefix());
        for (AddonsCommand.AddonCmds cmds : AddonsCommand.AddonCmds.values()) {
            if (cmds.cmd instanceof RTPCommandHelpable)
                list.add(((RTPCommandHelpable) cmds.cmd).getHelp().replace("%command%", label));
        }
        Main.getInstance().msgs.sms(sendi, list);
    }

    @Override
    public String getHelp() {
        return Main.getInstance().msgs.getBaseHelp();
    }
}
