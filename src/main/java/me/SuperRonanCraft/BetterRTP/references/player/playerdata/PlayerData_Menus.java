package me.SuperRonanCraft.BetterRTP.references.player.playerdata;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;

public class PlayerData_Menus {

    @Getter @Setter private Inventory inv;
    @Getter @Setter RTP_INV_SETTINGS invType;
    @Getter @Setter World invWorld;
    @Getter @Setter RTP_INV_SETTINGS invNextInv;

}
