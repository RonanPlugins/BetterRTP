package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface PartyCommandsTabable {

    List<String> tabComplete(CommandSender sendi, String[] args, AddonParty addon);

}
