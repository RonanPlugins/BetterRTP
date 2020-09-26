package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

class RTPDelay implements Listener {
    private int run;
    private final WorldPlayer pWorld;
    private final boolean cancelOnMove, cancelOnDamage;

    RTPDelay(CommandSender sendi, WorldPlayer pWorld, int delay, boolean cancelOnMove, boolean cancelOnDamage) {
        this.pWorld = pWorld;
        this.cancelOnMove = cancelOnMove;
        this.cancelOnDamage = cancelOnDamage;
        delay(sendi, delay);
    }

    private void delay(CommandSender sendi, int delay) {
        getPl().getRTP().getTeleport().beforeTeleportDelay(pWorld.getPlayer(), delay);
        run = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), run(sendi, this), delay * 20);
        if (cancelOnMove || cancelOnDamage)
            Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(PlayerMoveEvent e) {
        if (cancelOnMove)
            if (e.getPlayer().equals(pWorld.getPlayer()) &&
                (e.getTo() != null &&
                        (e.getTo().getBlockX() != e.getFrom().getBlockX() ||
                        e.getTo().getBlockY() != e.getFrom().getBlockY() ||
                        e.getTo().getBlockZ() != e.getFrom().getBlockZ()))
            ) {
                cancel();
            }
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(EntityDamageEvent e) {
        if (cancelOnDamage)
            if (e.getEntity() instanceof Player){
                if (e.getEntity().equals(pWorld.getPlayer()))
                    cancel();
            }
    }

    private void cancel() {
        Bukkit.getScheduler().cancelTask(run);
        if (!Bukkit.getScheduler().isCurrentlyRunning(run)) {
            HandlerList.unregisterAll(this);
            getPl().getRTP().getTeleport().cancelledTeleport(pWorld.getPlayer());
            getPl().getEco().unCharge(pWorld.getPlayer(), pWorld.getPrice());
            getPl().getCmd().cooldowns.remove(pWorld.getPlayer().getUniqueId());
            getPl().getCmd().rtping.put(pWorld.getPlayer().getUniqueId(), false);
        }
    }

    private Runnable run(final CommandSender sendi, final RTPDelay cls) {
        return () -> {
                HandlerList.unregisterAll(cls);
                if (getPl().getCmd().rtping.containsKey(pWorld.getPlayer().getUniqueId())) {
                    try {
                        getPl().getRTP().findSafeLocation(sendi, pWorld);
                    } catch (NullPointerException e) {
                        if (pWorld.getPrice() > 0)
                            getPl().getEco().unCharge(pWorld.getPlayer(), pWorld.getPrice());
                    }
                    getPl().getCmd().rtping.put(pWorld.getPlayer().getUniqueId(), false);
                } else if (pWorld.getPrice() > 0)
                    getPl().getEco().unCharge(pWorld.getPlayer(), pWorld.getPrice());
                Bukkit.getScheduler().cancelTask(run);
        };
    }

    private Main getPl() {
        return Main.getInstance();
    }
}
