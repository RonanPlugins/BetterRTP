package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class Listener implements org.bukkit.event.Listener {
    private final Join join = new Join();
    private final Leave leave = new Leave();
    private final Interact interact = new Interact();
    private final Click click = new Click();
    //private final PluginEnable pEnable = new PluginEnable();

    public void registerEvents(Main pl) {
        PluginManager pm = pl.getServer().getPluginManager();
        pm.registerEvents(this, pl);
    }

    public void load() {
        interact.load();
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void onLeave(PlayerQuitEvent e) {
        leave.event(e);
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void onJoin(PlayerJoinEvent e) {
        join.event(e);
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void onInteract(PlayerInteractEvent e) {
        interact.event(e);
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void interact(SignChangeEvent e) {
        interact.createSign(e);
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void click(InventoryClickEvent e) {
        click.click(e);
    }

    /*@EventHandler
    private void enablePlugin(PluginEnableEvent e) {
        pEnable.enabled(e);
    }*/
}