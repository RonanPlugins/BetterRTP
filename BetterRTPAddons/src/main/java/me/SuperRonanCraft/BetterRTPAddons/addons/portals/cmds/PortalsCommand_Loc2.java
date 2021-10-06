package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalsCommand_Loc2 implements PortalsCommands, LocationFinder {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addon) {
        Player p = (Player) sendi;
        Block block = getTargetBlock(p, 10);
        if (block != null) {
            Location loc = block.getLocation();
            addon.msgs.getLocation_2(sendi, loc);
            addon.getPortals().cachePortal(p, loc, true);
        } else {
            addon.msgs.getLocation_Look(sendi);
        }
    }
}
