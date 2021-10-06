package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.AddonParty;
import org.bukkit.command.CommandSender;

public interface PartyCommands {

    void execute(CommandSender sendi, String label, String[] args, AddonParty addonPortals);

}
