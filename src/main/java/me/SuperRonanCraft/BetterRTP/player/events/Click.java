package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.invs.RTPInventories;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Click {

    static void click(InventoryClickEvent e) {
        if (!validClick(e))
            return;
        e.setCancelled(true);
        handler(e);
    }

    static private void handler(InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            PlayerData data = HelperPlayer.getData(p);
            RTPInventories menu = BetterRTP.getInstance().getInvs();
            menu.getInv(data.getMenu().getInvType()).clickEvent(e);
        } catch (NullPointerException ex) {
            //ex.printStackTrace();
        }
    }

    static private boolean validClick(InventoryClickEvent e) {
        //Not a player, or Not our inventory
        if (!(e.getWhoClicked() instanceof Player) || e.isCancelled())
            return false;
            // Item is clicked
        else if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
            return false;
        else if (e.getWhoClicked() instanceof Player) {
            // Clicks the inventory
            PlayerData data = HelperPlayer.getData((Player) e.getWhoClicked());
            if (!e.getInventory().equals(data.getMenu().getInv()))
                return false;
                // Clicks their own inventory
            else if (!e.getClickedInventory().equals(data.getMenu().getInv())) {
                e.setCancelled(true);
                return false;
            }
        }
        return true;
    }
}
