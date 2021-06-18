package me.SuperRonanCraft.BetterRTPAddons.cmds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTPAddons.AddonMessages;
import me.SuperRonanCraft.BetterRTPAddons.AddonsHandler;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AddonsCommand_Version implements AddonsCommands, RTPCommandHelpable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        sendi.sendMessage(BetterRTP.getInstance().getText()
                .colorPre("&aVersion #&e" + Main.getInstance().getDescription().getVersion()));
    }

    @Override
    public String getHelp() {
        return Main.getInstance().msgs.getBaseList();
    }
}
