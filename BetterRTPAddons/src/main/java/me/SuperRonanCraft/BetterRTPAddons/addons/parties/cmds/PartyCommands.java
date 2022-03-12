package me.SuperRonanCraft.BetterRTPAddons.addons.parties.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.parties.AddonParty;
import org.bukkit.command.CommandSender;

public interface PartyCommands {

    void execute(CommandSender sendi, String label, String[] args, AddonParty addonPortals);

}
