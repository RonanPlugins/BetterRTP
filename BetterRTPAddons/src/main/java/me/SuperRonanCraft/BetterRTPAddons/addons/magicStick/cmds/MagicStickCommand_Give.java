package me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.AddonMagicStick;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MagicStickCommand_Give implements MagicStickCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonMagicStick addon) {
        if (args.length <= 2 && !(sendi instanceof Player)) {
            sendi.sendMessage("Console is not able to give themself this item!");
            return;
        }
        if (args.length <= 2) {
            Player p = (Player) sendi;
            p.getInventory().addItem(addon.events.item.clone());
            addon.msgs.getGiven(sendi);
        } else {
            Player p = null;
            for (Player plr : Bukkit.getOnlinePlayers()) {
                if (plr.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    p = plr;
                    break;
                }
            }
            if (p != null) {
                p.getInventory().addItem(addon.events.item.clone());
                addon.msgs.getGiven(p);
                addon.msgs.getGive(sendi, p.getName());
            } else
                addon.msgs.getPlayerError(sendi, args[2]);
        }
    }
}
