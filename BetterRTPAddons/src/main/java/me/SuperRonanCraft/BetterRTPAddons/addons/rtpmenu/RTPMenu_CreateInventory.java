package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTPMenu_CreateInventory {

    public static void createInv(AddonRTPMenu pl, Player p) {
        List<World> bukkit_worlds = Bukkit.getWorlds();
        List<String> display_worlds = Files.FILETYPE.CONFIG.getStringList(AddonRTPMenu.name + ".Worlds");
        List<World> actual_worlds = new ArrayList<>();
        for (int i = 0; i < 10; i++) //add x Junk worlds for testing
            for (World world : bukkit_worlds) {
                if (display_worlds.contains(world.getName()))
                    actual_worlds.add(world);
            }
        Inventory inv = createInventory(Files.FILETYPE.CONFIG.getString(AddonRTPMenu.name + ".Menu.Title"), Math.floorDiv(actual_worlds.size(), 9) * 9 + 9);

        HashMap<World, Integer> world_slots = centerWorlds(new ArrayList<>(actual_worlds));

        for (Map.Entry<World, Integer> world : world_slots.entrySet()) {
            int slot = world.getValue();
            ItemStack item = new ItemStack(Material.MAP, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(world.getKey().getName() + ": " + actual_worlds.size() + " " + world_slots.size());
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        pl.getData(p).setMenuInv(inv);
        p.openInventory(inv);
    }

    private static HashMap<World, Integer> centerWorlds(List<World> actual_worlds) {
        HashMap<World, Integer> map = new HashMap<>();
        while(actual_worlds.size() >= 9) {
            map.put(actual_worlds.get(0), map.size());
            actual_worlds.remove(0);
        }
        int slot = map.size();
        for (int i = 0; i < actual_worlds.size(); i++) {
            map.put(actual_worlds.get(i), slot + getSlotOffset(actual_worlds.size(), i) + i);
        }

        return map;
    }

    private static int getSlotOffset(int gear_to_show, int index) {
        if (gear_to_show % 2 == 0) { //Is Even
            switch (gear_to_show) {
                case 2:
                    switch (index) {
                        case 0: return 3;
                        case 1: return 4;
                    }
                    break;
                case 4:
                    switch (index) {
                        case 0:
                        case 1: return 2;
                        case 2:
                        case 3: return 3;
                    }
                    break;
                case 6:
                    switch (index) {
                        case 0:
                        case 1:
                        case 2: return 1;
                        case 3:
                        case 4:
                        case 5: return 2;
                    }
                    break;
                case 8:
                    if (index > 4) return 1;
            }
        } else {
            switch (gear_to_show) {
                case 1: return 4;
                case 3: return 3;
                case 5: return 2;
                case 7: return 1;
            }
        }
        return 0;
    }

    private static Inventory createInventory(String title, int size) {
        title = BetterRTP.getInstance().getText().color(title);
        return Bukkit.createInventory(null, Math.min(size, 54), title);
    }

}
