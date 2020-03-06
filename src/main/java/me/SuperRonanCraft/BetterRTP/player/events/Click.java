package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.PlayerInfo;
import me.SuperRonanCraft.BetterRTP.references.invs.RTPInventories;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Click {

    public void click(InventoryClickEvent e) {
        if (!validClick(e))
            return;
        e.setCancelled(true);
        handler(e);
    }

    private void handler(InventoryClickEvent e) {
        try {
            PlayerInfo pInfo = Main.getInstance().getpInfo();
            Player p = (Player) e.getWhoClicked();
            RTPInventories menu = Main.getInstance().getInvs();
            menu.getInv(pInfo.getInvType(p)).clickEvent(e);
        } catch (NullPointerException ex) {
            //ex.printStackTrace();
        }
    }

    private boolean validClick(InventoryClickEvent e) {
        //Not a player, or Not our inventory
        if (!(e.getWhoClicked() instanceof Player) || e.isCancelled())
            return false;
            // Item is clicked
        else if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
            return false;
        else if (e.getWhoClicked() instanceof Player) {
            // Clicks the inventory
            if (!e.getInventory().equals(Main.getInstance().getpInfo().getInv((Player) e.getWhoClicked())))
                return false;
                // Clicks their own inventory
            else if (!e.getClickedInventory().equals(Main.getInstance().getpInfo().getInv((Player) e
                    .getWhoClicked()))) {
                e.setCancelled(true);
                return false;
            }
        }
        return true;
    }
}
