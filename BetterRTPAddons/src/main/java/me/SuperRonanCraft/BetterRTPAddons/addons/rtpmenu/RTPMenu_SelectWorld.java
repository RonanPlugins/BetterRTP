package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdTeleport;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTPMenu_SelectWorld {

    public static void createInv(AddonRTPMenu pl, Player p) {
        List<World> bukkit_worlds = Bukkit.getWorlds();
        List<World> actual_worlds = new ArrayList<>();
        for (World world : bukkit_worlds) {
            if (pl.getWorlds().containsKey(world.getName()) && PermissionNode.getAWorld(p, world.getName()))
                actual_worlds.add(world);
        }
        if (actual_worlds.isEmpty() || (actual_worlds.size() <= 1 && !BetterRTP.getInstance().getSettings().isDebug())) {
            CmdTeleport.teleport(p, "rtp", null, null);
            return;
        }
        int size = Math.floorDiv(actual_worlds.size(), 9) * 9;
        if (size < actual_worlds.size()) size += 9;
        Inventory inv = createInventory(color(p, Files.FILETYPE.CONFIG.getString(AddonRTPMenu.name + ".Title")), size);

        HashMap<Integer, World> world_slots = centerWorlds(new ArrayList<>(actual_worlds));

        for (Map.Entry<Integer, World> world : world_slots.entrySet()) {
            String worldName = world.getValue().getName();
            RTPMenuWorldInfo worldInfo = pl.getWorlds().getOrDefault(worldName, new RTPMenuWorldInfo(worldName, Material.MAP, null));
            int slot = world.getKey();
            ItemStack item = new ItemStack(worldInfo.item, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(color(p, worldInfo.name));
            List<String> lore = new ArrayList<>(worldInfo.lore);
            lore.forEach(s -> lore.set(lore.indexOf(s), color(p, s).replace("%world%", world.getValue().getName())));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        pl.getData(p).setMenuInv(inv);
        pl.getData(p).setWorldSlots(world_slots);
        p.openInventory(inv);
    }

    private static HashMap<Integer, World> centerWorlds(List<World> actual_worlds) {
        HashMap<Integer, World> map = new HashMap<>();
        while(actual_worlds.size() >= 9) {
            for (int i = 0; i < 9; i++) {
                map.put(map.size(), actual_worlds.get(0));
                actual_worlds.remove(0);
            }
        }
        int slot = map.size();
        //BetterRTP.getInstance().getLogger().log(Level.INFO, "- " + actual_worlds.size());
        for (int i = 0; i < actual_worlds.size(); i++) {
            int offset = getSlotOffset(actual_worlds.size(), i);
            //BetterRTP.getInstance().getLogger().log(Level.INFO, "- " + offset);
            map.put(slot + offset + i, actual_worlds.get(i));
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
                    if (index >= 4) return 1;
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

    private static String color(CommandSender sendi, String str) {
        return ChatColor.translateAlternateColorCodes('&', Message.placeholder(sendi, str));
    }

    private static Inventory createInventory(String title, int size) {
        title = Message.color(title);
        return Bukkit.createInventory(null, Math.max(Math.min(size, 54), 9), title);
    }

}
