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
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addon) {
        if (!(sendi instanceof Player)) {
            sendi.sendMessage("Console cannot perform this command!");
            return;
        }
        //Valid Name
        if (args.length < 3 || args[2].length() < 1) {
            addon.msgs.getCreateName(sendi);
            return;
        }
        Player p = (Player) sendi;
        PortalsRegionInfo portal = addon.getPortals().getPortal(p);
        //Valid region
        if (    portal == null ||
                portal.getLoc1() == null ||
                portal.getLoc2() == null ||
                portal.getLoc1().getWorld() != portal.getLoc2().getWorld()) {
            addon.msgs.getCreateInvalid(sendi);
            return;
        }
        String name = args[2];
        if (args.length > 3) {
            portal.setWorld(args[3]);
        }
        //Duplicate Name
        for (PortalsRegionInfo portals : addon.getPortals().getRegisteredPortals()) {
            if (portals.getName().equals(name)) {
                addon.msgs.getCreateDuplicate(sendi);
                return;
            }
        }
        if (addon.getPortals().addRegisteredPortal(p, name)) {
            if (portal.getWorld() == null)
                addon.msgs.getCreateConfirm(p, name);
            else
                addon.msgs.getCreateConfirmWorld(p, name, portal.getWorld());
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
