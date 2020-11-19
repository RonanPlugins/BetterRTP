package me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.AddonMagicStick;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.command.CommandSender;

public class MagicStickCommand_Give implements MagicStickCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonMagicStick addon) {
        sendi.sendMessage("magicstick give command!");
    }
}
