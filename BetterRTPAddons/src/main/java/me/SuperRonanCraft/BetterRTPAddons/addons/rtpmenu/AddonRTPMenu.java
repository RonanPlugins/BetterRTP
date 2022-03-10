package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdTeleport;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CommandEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Objects;

public class AddonRTPMenu implements Addon, Listener {

    public  static String name = "RTPMenu";
    private final HashMap<Player, MenuData> playerData = new HashMap<>();

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
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
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
            CmdTeleport.teleport(e.getWhoClicked(), "rtp", data.getWorldSlots().get(e.getSlot()).getName(), null);
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    private void onTeleport(RTP_CommandEvent e) {
        if (e.getCmd() instanceof CmdTeleport && e.getSendi() instanceof Player) {
            e.setCancelled(true);
            RTPMenu_SelectWorld.createInv(this, (Player) e.getSendi());
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
