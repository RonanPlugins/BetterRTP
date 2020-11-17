package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.PortalsCreation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalsCommand_Create implements PortalsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals) {
        sendi.sendMessage("Portals Create command!");
        PortalsCreation portal = addonPortals.getPortals().getPortal((Player) sendi);
        if (portal == null || portal.getLoc1() == null || portal.getLoc2() == null) {
            sendi.sendMessage("Invalid portal!");
            return;
        }
        sendi.sendMessage("Valid portal!");
    }
}
