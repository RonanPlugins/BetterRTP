package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalsCommand_Loc1 implements PortalsCommands, LocationFinder {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals) {
        Player p = (Player) sendi;
        Location loc = getTargetBlock(p, 10).getLocation();
        addonPortals.getPortals().setPortal(p, loc, false);
        sendi.sendMessage("Location 1 set to this location " + loc.toString());
        p.sendBlockChange(loc, Material.GLOWSTONE, (byte) 0);
    }
}
