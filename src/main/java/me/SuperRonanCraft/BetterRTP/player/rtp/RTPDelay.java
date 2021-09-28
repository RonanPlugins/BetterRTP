package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CancelledEvent;
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
    private final boolean cancelOnMove, cancelOnDamage;
    private final RTPPlayer rtp;

    RTPDelay(CommandSender sendi, RTPPlayer rtp, int delay, boolean cancelOnMove, boolean cancelOnDamage) {
        this.cancelOnMove = cancelOnMove;
        this.cancelOnDamage = cancelOnDamage;
        this.rtp = rtp;
        delay(sendi, delay);
    }

    private void delay(CommandSender sendi, int delay) {
        getPl().getRTP().getTeleport().beforeTeleportDelay(rtp.getPlayer(), delay);
        run = Bukkit.getScheduler().scheduleSyncDelayedTask(BetterRTP.getInstance(), run(sendi, this), delay * 20L);
        if (cancelOnMove || cancelOnDamage)
            Bukkit.getPluginManager().registerEvents(this, BetterRTP.getInstance());
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(PlayerMoveEvent e) {
        if (cancelOnMove)
            if (e.getPlayer().equals(rtp.getPlayer()) &&
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
                if (e.getEntity().equals(rtp.getPlayer()))
                    cancel();
            }
    }

    private void cancel() {
        Bukkit.getScheduler().cancelTask(run);
        if (!Bukkit.getScheduler().isCurrentlyRunning(run)) {
            HandlerList.unregisterAll(this);
            getPl().getRTP().getTeleport().cancelledTeleport(rtp.getPlayer());
            //getPl().getEco().unCharge(rtp.getPlayer(), rtp.pWorld);
            getPl().getCooldowns().removeCooldown(rtp.getPlayer());
            getPl().getCmd().rtping.put(rtp.getPlayer().getUniqueId(), false);
            Bukkit.getServer().getPluginManager().callEvent(new RTP_CancelledEvent(rtp.getPlayer()));
        }
    }

    private Runnable run(final CommandSender sendi, final RTPDelay cls) {
        return () -> {
                HandlerList.unregisterAll(cls);
                if (getPl().getCmd().rtping.containsKey(rtp.getPlayer().getUniqueId()))
                    rtp.randomlyTeleport(sendi);
        };
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
