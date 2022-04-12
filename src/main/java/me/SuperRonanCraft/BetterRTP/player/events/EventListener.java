package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;

public class EventListener implements Listener {
    private final Interact interact = new Interact();

    public void registerEvents(BetterRTP pl) {
        PluginManager pm = pl.getServer().getPluginManager();
        pm.registerEvents(this, pl);
    }

    public void load() {
        interact.load();
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        Leave.event(e);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Join.event(e);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        interact.event(e);
    }

    @EventHandler
    private void interact(SignChangeEvent e) {
        interact.createSign(e);
    }

    @EventHandler
    private void click(InventoryClickEvent e) {
        Click.click(e);
    }

    @EventHandler
    private void teleport(PlayerTeleportEvent e) {
        Teleport.tpEvent(e);
    }

    @EventHandler
    private void rtpPost(RTP_TeleportPostEvent e) {
        Custom.postRTP(e);
    }
}