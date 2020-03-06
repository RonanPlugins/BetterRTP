package me.SuperRonanCraft.BetterRTP.references.invs.types;

import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import me.SuperRonanCraft.BetterRTP.references.invs.enums.RTPInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RTPInvMain extends RTPInventory {

    public void show(Player p) {
        Inventory inv = this.createInv(9, "&lSettings");
        int _index = 0;
        for (RTP_INV_SETTINGS type : RTP_INV_SETTINGS.values()) {
            if (type.getShowMain()) {
                String _name = type.name();
                _name = _name.substring(0, 1).toUpperCase() + _name.substring(1).toLowerCase();
                ItemStack _item = createItem("paper", 1, _name, null);
                inv.setItem(_index, _item);
                _index ++;
            }
        }
        p.openInventory(inv);
        this.cacheInv(p, inv, this.type);
    }

    @Override
    public void clickEvent(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        int _index = 0;
        for (RTP_INV_SETTINGS type : RTP_INV_SETTINGS.values()) {
            if (type.getShowMain()) {
                if (_index == slot) {
                    type.getInv().show(p);
                    return;
                }
                _index++;
            }
        }
    }
}