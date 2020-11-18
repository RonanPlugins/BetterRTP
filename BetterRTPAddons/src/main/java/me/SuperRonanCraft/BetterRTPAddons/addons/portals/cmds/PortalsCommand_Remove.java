package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalsCommand_Remove implements PortalsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals) {
        if (args.length < 3) {
            addonPortals.msgs.usageRemove(sendi, label);
            return;
        }
        String portalName = args[2];
        for (PortalsRegionInfo portal : addonPortals.getPortals().getRegisteredPortals()) {
            if (portal.getName().equals(portalName)) {
                if (addonPortals.getPortals().removeRegisteredPortal(portal))
                    addonPortals.msgs.getRemoveConfirm(sendi, portal.getName());
                else
                    sendi.sendMessage("Error Removing portal " + portalName);
                return;
            }
        }
        //None found
        addonPortals.msgs.getRemoveNone(sendi, portalName);
    }
}
