package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PortalsCommand_Create implements PortalsCommands, PortalsCommandsTabable {

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
        if (args.length > 3) {
            portal.setWorld(args[3]);
        }
        //Duplicate Name
        for (PortalsRegionInfo portals : addonPortals.getPortals().getRegisteredPortals()) {
            if (portals.getName().equals(name)) {
                addonPortals.msgs.getCreateDuplicate(sendi);
                return;
            }
        }
        if (addonPortals.getPortals().addRegisteredPortal(p, name)) {
            if (portal.getWorld() == null)
                addonPortals.msgs.getCreateConfirm(p, name);
            else
                addonPortals.msgs.getCreateConfirmWorld(p, name, portal.getWorld());
        } else
            sendi.sendMessage("Something went wrong when creating a portal!");
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args, AddonPortals addonPortals) {
        List<String> list = new ArrayList<>();
        if (args.length == 4) {
            for (World world : Bukkit.getWorlds())
                if (world.getName().toLowerCase().startsWith(args[3].toLowerCase()))
                    list.add(world.getName());
        }
        return list;
    }
}
