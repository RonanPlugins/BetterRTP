package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public interface LocationFinder {

    default Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = null;
        while (iter.hasNext()) {
            Block block = iter.next();
            if (block.getType() != Material.AIR) {
                lastBlock = block;
                break;
            }
        }
        return lastBlock;
    }

}
