package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import org.bukkit.command.CommandSender;

public class PortalsCommand_Create implements PortalsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        sendi.sendMessage("Portals Create command!");
    }
}
