package me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.AddonMagicStick;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MagicStickCommand_Give implements MagicStickCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonMagicStick addon) {
        Player p = (Player) sendi;
        p.getWorld().dropItem(p.getLocation(), addon.events.item.clone());
    }
}
