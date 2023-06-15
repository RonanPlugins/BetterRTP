package me.SuperRonanCraft.BetterRTP.references.invs.types;

import me.SuperRonanCraft.BetterRTP.references.invs.enums.RTPInventory;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther.FILETYPE;
import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import me.SuperRonanCraft.BetterRTP.references.invs.enums.RTP_INV_ITEMS;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RTPInvCoordinates extends RTPInventory {

    public void show(Player p) {
        if (BetterRTP.getInstance().getPInfo().getInvWorld().get(p) == null) {
            BetterRTP.getInstance().getPInfo().setNextInv(p, this.type);
            BetterRTP.getInstance().getInvs().getInv(RTP_INV_SETTINGS.WORLDS).show(p);
            return;
        }
        int slots = (RTP_COORDINATES_SETTINGS.values().length - (RTP_COORDINATES_SETTINGS.values().length % 9) + 1) * 9;
        if (slots < 6 * 9)
            slots += 9;
        Inventory inv = this.createInv(slots, "Settings: &lCoordinates");
        int index = 0;
        for (RTP_COORDINATES_SETTINGS set : RTP_COORDINATES_SETTINGS.values()) {
            ItemStack _item = createItem(RTP_INV_ITEMS.NORMAL.item, RTP_INV_ITEMS.NORMAL.amt, "&a&l" + set.getInfo()[1], null);
            inv.setItem(index, _item);
            index++;
        }
        ItemStack _item = createItem(RTP_INV_ITEMS.BACK.item, RTP_INV_ITEMS.BACK.amt, RTP_INV_ITEMS.BACK.name, null);
        inv.setItem(inv.getSize() - 9 + RTP_INV_ITEMS.BACK.slot, _item);
        p.openInventory(inv);
        this.cacheInv(p, inv, this.type);
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        int slot = e.getSlot();
        for (RTP_COORDINATES_SETTINGS set : RTP_COORDINATES_SETTINGS.values()) {
            
        }
        for (RTP_INV_ITEMS type : RTP_INV_ITEMS.values()) {
            if (type.slot != -1) {
                switch (type) {
                    case BACK:
                        if (slot == e.getInventory().getSize() - 9 + type.slot)
                            BetterRTP.getInstance().getInvs().getInv(RTP_INV_SETTINGS.MAIN).show((Player) e.getWhoClicked());
                    default:
                        break;
                }
            }
        }
    }
}

enum RTP_COORDINATES_SETTINGS {
    CENTER_Z(   SETTINGS_TYPE.INTEGER, FILETYPE.CONFIG, "Settings.Importance.Enabled",
            new Object[]{true, "Center Z", "&7Toggle Categories system", "paper"}),
    CENTER_X(   SETTINGS_TYPE.INTEGER, FILETYPE.CONFIG, "Settings.Cooldown.Enabled",
            new Object[]{true, "Center X", "&7Toggle Cooldown system", "paper"}),
    USE_WORLDBORDER( SETTINGS_TYPE.BOOLEAN, FILETYPE.CONFIG, "Settings.Cooldown.Time",
            new Object[]{true, "Cooldown Time", new ArrayList<String>() {{
                add("&7Set the time (in minutes)");
                add("&7between making a new ticket");
            }}, "paper"}),
    /*DEBUG(      SETTINGS_TYPE.BOOLEAN, FILETYPE.CONFIG, "Settings.Debug",
            new Object[]{true, "Debug", "&7Toggle debugger", "paper"}),
    TEMPLATE(   SETTINGS_TYPE.BOOLEAN, FILETYPE.CONFIG, "Template.Enabled",
            new Object[]{true, "Templates", "&7Toggle Templates system", "paper"});*/;

    SETTINGS_TYPE type;
    FILETYPE filetype;
    String path;
    String[] condition = null;
    Object[] info; // = new Object[]{false}; //ENABLED, NAME, DESCRIPTION, ITEM

    RTP_COORDINATES_SETTINGS(SETTINGS_TYPE type, FILETYPE filetype, String path, Object[] info) {
        this.type = type;
        this.filetype = filetype;
        this.path = path;
        this.info = info;
    }

    RTP_COORDINATES_SETTINGS(SETTINGS_TYPE type, FILETYPE filetype, String[] arry, Object[] info) {
        this.type = type;
        this.filetype = filetype;
        this.path = null;
        this.info = info;
        //Condition
        this.condition = arry;
    }

    void setValue(Object value) {
        BetterRTP.getInstance().getFiles().getType(filetype).setValue(path, value);
    }

    public Object[] getInfo() {return info;}

    public Object getValue() {
        String path = this.path;
        if (path == null && condition != null) {
            if (BetterRTP.getInstance().getFiles().getType(filetype).getBoolean(condition[0]))
                path = condition[1];
            else
                path = condition[2];
        }
        return getValuePath(path);
    }

    private Object getValuePath(String path) {
        if (path != null) {
            if (getType() == SETTINGS_TYPE.BOOLEAN)
                return BetterRTP.getInstance().getFiles().getType(filetype).getBoolean(path);
            else if (getType() == SETTINGS_TYPE.STRING)
                return BetterRTP.getInstance().getFiles().getType(filetype).getString(path);
            else if (getType() == SETTINGS_TYPE.INTEGER)
                return BetterRTP.getInstance().getFiles().getType(filetype).getInt(path);
        }
        return null;
    }

    public SETTINGS_TYPE getType() {
        return type;
    }

    public FILETYPE getFiletype() {
        return filetype;
    }

    enum SETTINGS_TYPE {
        BOOLEAN(Boolean.class), STRING(String.class), INTEGER(Integer.class);

        private Class cla;

        SETTINGS_TYPE(Class cla) {
            this.cla = cla;
        }

        Class getCla() {
            return cla;
        }
    }
}