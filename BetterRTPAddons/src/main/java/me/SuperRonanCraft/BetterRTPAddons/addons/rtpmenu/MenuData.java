package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class MenuData {

    @Getter @Setter Inventory menuInv;
    @Getter @Setter HashMap<Integer, World> worldSlots;

}
