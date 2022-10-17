package me.SuperRonanCraft.BetterRTPAddons.cmds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import me.SuperRonanCraft.BetterRTPAddons.AddonMessages;
import me.SuperRonanCraft.BetterRTPAddons.AddonsHandler;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.util.Message_ADDONS;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AddonsCommand_Version implements AddonsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        Message_ADDONS.sms(sendi, "&aVersion #&e" + Main.getInstance().getDescription().getVersion());
    }
}
