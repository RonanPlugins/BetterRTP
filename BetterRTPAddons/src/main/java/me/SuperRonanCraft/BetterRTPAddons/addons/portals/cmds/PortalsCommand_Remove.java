package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PortalsCommand_Remove implements PortalsCommands, PortalsCommandsTabable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addon) {
        if (args.length < 3) {
            addon.msgs.usageRemove(sendi, label);
            return;
        }
        String portalName = args[2];
        for (PortalsRegionInfo portal : addon.getPortals().getRegisteredPortals()) {
            if (portal.getName().equals(portalName)) {
                if (addon.getPortals().removeRegisteredPortal(portal))
                    addon.msgs.getRemoveConfirm(sendi, portal.getName());
                else
                    sendi.sendMessage("Error Removing portal " + portalName);
                return;
            }
        }
        //None found
        addon.msgs.getRemoveNone(sendi, portalName);
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args, AddonPortals addonPortals) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (PortalsRegionInfo portal : addonPortals.getPortals().getRegisteredPortals()) {
                if (portal.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    list.add(portal.getName());
            }
        }
        return list;
    }
}
