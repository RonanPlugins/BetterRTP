package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import com.comphenix.protocol.events.PacketContainer;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PortalsCommand_Loc1 implements PortalsCommands, LocationFinder {

    private PacketContainer fakeBlock;

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals) {
        Player p = (Player) sendi;
        Block block = getTargetBlock(p, 10);
        if (block != null) {
            Location loc = block.getLocation();
            addonPortals.getPortals().cachePortal(p, loc, false);
            addonPortals.msgs.getLocation_1(sendi);
        } else {
            addonPortals.msgs.getLocation_Look(sendi);
        }
    }
}
