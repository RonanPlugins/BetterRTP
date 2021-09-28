package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.invs.RTPInventories;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.PlayerInfo;
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
            PlayerInfo pInfo = BetterRTP.getInstance().getpInfo();
            Player p = (Player) e.getWhoClicked();
            RTPInventories menu = BetterRTP.getInstance().getInvs();
            menu.getInv(pInfo.getInvType().get(p)).clickEvent(e);
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
            if (!e.getInventory().equals(BetterRTP.getInstance().getpInfo().getInvs().get((Player) e.getWhoClicked())))
                return false;
                // Clicks their own inventory
            else if (!e.getClickedInventory().equals(BetterRTP.getInstance().getpInfo().getInvs().get((Player) e
                    .getWhoClicked()))) {
                e.setCancelled(true);
                return false;
            }
        }
        return true;
    }
}
