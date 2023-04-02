package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdTeleport;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CommandEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.security.auth.login.Configuration;
import java.util.*;

public class AddonRTPMenu implements Addon, Listener {

    public  static String name = "RTPMenu";
    private final HashMap<Player, MenuData> playerData = new HashMap<>();
    @Getter private HashMap<String, RTPMenuWorldInfo> worlds = new HashMap<>();

    public MenuData getData(Player p) {
        if (!playerData.containsKey(p))
            playerData.put(p, new MenuData());
        return playerData.getOrDefault(p, null);
    }

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        for (Player p : playerData.keySet())
            p.closeInventory();
        playerData.clear();
        ConfigurationSection config = getFile(Files.FILETYPE.CONFIG).getConfigurationSection("RTPMenu");
        List<Map<?, ?>> map = config.getMapList("WorldList");
        for (Map<?, ?> m : map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                String world = entry.getKey().toString();
                Map<?, ?> test = ((Map<?, ?>) m.get(world));
                if (test == null)
                    continue;
                String item = "MAP";
                String name = world;
                List<String> lore = new ArrayList<>();
                if (test.get("Item") != null) {
                    if (test.get("Item").getClass() == String.class)
                        item = String.valueOf(test.get("Item").toString());
                }
                if (test.get("Name") != null) {
                    if (test.get("Name").getClass() == String.class)
                        name = String.valueOf(test.get("Name").toString());
                }
                if (test.get("Lore") != null) {
                    if (test.get("Lore").getClass() == ArrayList.class)
                        lore = new ArrayList<String>((ArrayList) test.get("Lore"));
                }
                try {
                    worlds.put(world, new RTPMenuWorldInfo(name, Material.valueOf(item.toUpperCase()), lore));
                } catch (IllegalArgumentException | NullPointerException e) {
                    Main.getInstance().getLogger().warning("The item " + item + " is an unknown item id! Set to a map!");
                }
            }
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
        for (Player p : playerData.keySet())
            p.closeInventory();
    }

    @Override
    public RTPCommand getCmd() {
        return null;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if (validClick(e)) {
            e.setCancelled(true);
            MenuData data = getData((Player) e.getWhoClicked());
            CmdTeleport.teleport(e.getWhoClicked(), "rtp", data.getWorldSlots().get(e.getSlot()), null);
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    private void onTeleport(RTP_CommandEvent e) {
        if (e.getCmd() instanceof CmdTeleport && e.getSendi() instanceof Player) {
            e.setCancelled(true);
            int refresh = Files.FILETYPE.CONFIG.getInt(AddonRTPMenu.name + ".AutoRefresh");
            Player player = (Player) e.getSendi();
            new RTPMenu_Refresh(this, player, refresh);
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
            MenuData data = playerData.getOrDefault((Player) e.getWhoClicked(), null);
            if (data != null) {
                if (!e.getInventory().equals(data.getMenuInv()))
                    return false;
                    // Clicks their own inventory
                else if (!Objects.equals(e.getClickedInventory(), data.getMenuInv())) {
                    e.setCancelled(true);
                    return false;
                }
            } else
                return false;
        }
        return true;
    }
}
