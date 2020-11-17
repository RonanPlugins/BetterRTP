package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class PortalsCommand_Loc2 implements PortalsCommands, LocationFinder {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals) {
        Player p = (Player) sendi;
        Location loc = getTargetBlock(p, 10).getLocation();
        addonPortals.getPortals().setPortal(p, loc, true);
        sendi.sendMessage("Location 2 set to this location!");
    }
}
