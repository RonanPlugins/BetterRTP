package me.SuperRonanCraft.BetterRTPAddons.portals.cmds;

import org.bukkit.command.CommandSender;

public class PortalsCommand_Loc1 implements PortalsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        sendi.sendMessage("Portals Loc1 command!");
    }
}
