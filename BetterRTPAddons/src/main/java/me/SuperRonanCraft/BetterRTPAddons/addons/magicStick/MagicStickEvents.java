package me.SuperRonanCraft.BetterRTPAddons.addons.magicStick;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CancelledEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.cmds.MagicStickCommand;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class MagicStickEvents implements Listener {

    public ItemStack item;
    boolean take;

    private final List<PlayerListener> teleportingPlayers = new ArrayList<>();

    void load() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        Files.FILETYPE file = Main.getInstance().getFiles().getType(Files.FILETYPE.CONFIG);
        String title = file.getString("MagicStick.Item.Name");
        Material mat = Material.valueOf(file.getString("MagicStick.Item.Material").toUpperCase());
        List<String> lore = file.getStringList("MagicStick.Item.Lore");
        item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(BetterRTP.getInstance().getText().color(title));
        meta.setLore(lore);
        lore.forEach((str) -> lore.set(lore.indexOf(str), BetterRTP.getInstance().getText().color(str)));
        item.setItemMeta(meta);

        this.take = file.getBoolean("MagicStick.Take");
    }

    void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    void use(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack eItem = e.getItem();
            if (eItem != null && eItem.getType() == item.getType()) {
                if (Objects.equals(eItem.getItemMeta(), item.getItemMeta())) {
                    //Not currently rtp'ing
                    for (PlayerListener listener : teleportingPlayers)
                        if (listener.p == e.getPlayer())
                            return;
                    //Rtp the player
                    BetterRTP.getInstance().getCmd().tp(e.getPlayer(), e.getPlayer(), e.getPlayer().getWorld().getName(), null, RTP_TYPE.ADDON_MAGICSTICK);
                    if (this.take)
                        teleportingPlayers.add(new PlayerListener(e.getPlayer()));
                }
            }
        }
    }

    private class PlayerListener implements Listener {
        Player p;

        PlayerListener(Player p) {
            this.p = p;
            load();
        }

        void load() {
            Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
        }

        void unload() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        void tp(RTP_TeleportPostEvent e) {
            if (e.getPlayer() == p) {
                if (e.getType() == RTP_TYPE.ADDON_MAGICSTICK)
                    e.getPlayer().getInventory().removeItem(item);
                teleportingPlayers.remove(this);
                this.unload();
            }
        }

        @EventHandler
        void drop(PlayerDropItemEvent e) {
            if (e.getPlayer() == p)
                e.setCancelled(true);
        }

        @EventHandler
        void cancelled(RTP_CancelledEvent e) {
            if (e.getPlayer() == p) {
                teleportingPlayers.remove(this);
                this.unload();
            }
        }
    }

}
