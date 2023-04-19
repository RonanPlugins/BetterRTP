package me.SuperRonanCraft.BetterRTPAddons.addons.rtpmenu;

import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RTPMenu_Refresh implements Listener {

    private final AddonRTPMenu addon;
    private final Player player;
    private final int time;
    private BukkitTask task;
    private boolean opening = true;

    RTPMenu_Refresh(AddonRTPMenu addon, Player player, int time) {
        this.addon = addon;
        this.player = player;
        this.time = time;
        if (time > 0) {
            Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
            start();
        } else {
            RTPMenu_SelectWorld.createInv(addon, player);
        }
    }

    private void start() {
        opening = true;
        if (!RTPMenu_SelectWorld.createInv(addon, player)) {
            cancel();
            return;
        }
        opening = false;
        this.task = new BukkitRunnable() {
            @Override public void run() {
                start();
            }
        }.runTaskLater(Main.getInstance(), time);
    }

    @EventHandler
    void close(InventoryCloseEvent e) {
        if (e.getPlayer() == player && !opening) {
            cancel();
        }
    }

    @EventHandler
    void leave(PlayerQuitEvent e) {
        if (e.getPlayer() == player) {
            cancel();
        }
    }

    void cancel() {
        if (task != null)
            task.cancel();
        HandlerList.unregisterAll(this);
    }

}
