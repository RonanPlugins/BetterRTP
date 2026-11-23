package me.SuperRonanCraft.BetterRTP.player.rtp;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CancelledEvent;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

class RTPDelay implements Listener {
    private WrappedTask task;
    private final boolean cancelOnMove, cancelOnDamage;
    private final RTPPlayer rtp;

    RTPDelay(CommandSender sendi, RTPPlayer rtp, int delay, boolean cancelOnMove, boolean cancelOnDamage) {
        this.cancelOnMove = cancelOnMove;
        this.cancelOnDamage = cancelOnDamage;
        this.rtp = rtp;
        rtp.getOperation().onFinish(this::dispose);
        delay(sendi, delay);
    }

    private void delay(CommandSender sendi, int delay) {
        rtp.afterPreEvent(getPl().getRTP().getTeleport().beforeTeleportDelay(rtp.getPlayer(), delay), () -> {
            task = AsyncHandler.syncLater(run(sendi, this), delay * 20L);
            if (cancelOnMove || cancelOnDamage)
                Bukkit.getPluginManager().registerEvents(this, BetterRTP.getInstance());
            if (!rtp.isActive()) dispose();
        });
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

    private void dispose() {
        dispose(task, this);
    }

    static void dispose(WrappedTask task, Listener listener) {
        try {
            if (task != null) task.cancel();
        } finally {
            HandlerList.unregisterAll(listener);
        }
    }

    private void cancel() {
        rtp.runSafely(() -> {
            try {
                getPl().getRTP().getTeleport().cancelledTeleport(rtp.getPlayer());
                getPl().getCooldowns().removeCooldown(rtp.getPlayer(), rtp.worldPlayer.getWorld());
            } finally {
                rtp.getOperation().finish();
            }
            Bukkit.getServer().getPluginManager().callEvent(new RTP_CancelledEvent(rtp.getPlayer()));
        });
    }

    private Runnable run(final CommandSender sendi, final RTPDelay cls) {
        return () -> rtp.runSafely(() -> {
            HandlerList.unregisterAll(cls);
            rtp.randomlyTeleport(sendi);
        });
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
