package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.command.CommandSender;

public interface PortalsCommands {

    void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals);

}
