package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalsCommand_Create implements PortalsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals) {
        if (!(sendi instanceof Player)) {
            sendi.sendMessage("Console cannot perform this command!");
            return;
        }
        Player p = (Player) sendi;
        PortalsRegionInfo portal = addonPortals.getPortals().getPortal(p);
        //Valid region
        if (    portal == null ||
                portal.getLoc1() == null ||
                portal.getLoc2() == null ||
                portal.getLoc1().getWorld() != portal.getLoc2().getWorld()) {
            addonPortals.msgs.getCreateInvalid(sendi);
            return;
        }
        //Valid Name
        if (args.length < 3 || args[2].length() < 1) {
            addonPortals.msgs.getCreateName(sendi);
            return;
        }
        String name = args[2];
        //Duplicate Name
        for (PortalsRegionInfo portals : addonPortals.getPortals().getRegisteredPortals()) {
            if (portals.getName().equals(name)) {
                addonPortals.msgs.getCreateDuplicate(sendi);
                return;
            }
        }
        if (addonPortals.getPortals().addRegisteredPortal(p, name))
            addonPortals.msgs.getCreateConfirm(sendi, name);
        else
            sendi.sendMessage("Something went wrong when creating a portal!");
    }
}
