package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import org.bukkit.Material;

import java.util.List;

public class RTPMenuWorldInfo {

    public final String name;
    public final Material item;
    public final List<String> lore;
    public final int slot;

    RTPMenuWorldInfo(String name, Material item, List<String> lore, int slot) {
        this.name = name;
        this.item = item;
        this.lore = lore;
        this.slot = slot;
    }
}
