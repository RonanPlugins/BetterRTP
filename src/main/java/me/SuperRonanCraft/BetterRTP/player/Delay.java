package me.SuperRonanCraft.BetterRTP.player;

import me.SuperRonanCraft.BetterRTP.references.worlds.PlayerWorld;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

class Delay implements Listener {
    private int run;
    private PlayerWorld pWorld;
    private boolean cancelOnMove, cancelOnDamage;

    Delay(CommandSender sendi, PlayerWorld pWorld, int delay, boolean cancelOnMove, boolean cancelOnDamage) {
        this.pWorld = pWorld;
        this.cancelOnMove = cancelOnMove;
        this.cancelOnDamage = cancelOnDamage;
        delay(sendi, delay);
    }

    @SuppressWarnings("deprecation")
    private void delay(CommandSender sendi, int delay) {
        Main pl = Main.getInstance();
        if (sendi.equals(pWorld.getPlayer()) && delay != 0 && getPl().getText().getTitleDelayChat())
            getPl().getText().getDelay(sendi, String.valueOf(delay));
        if (getPl().getText().getSoundsEnabled()) {
            Sound sound = getPl().getText().getSoundsDelay();
            if (sound != null)
                pWorld.getPlayer().playSound(pWorld.getPlayer().getLocation(), sound, 1F, 1F);
        }
        if (getPl().getText().getTitleEnabled()) {
            String title = getPl().getText().getTitleDelay(pWorld.getPlayer().getName(), String.valueOf(delay));
            String subTitle = getPl().getText().getSubTitleDelay(pWorld.getPlayer().getName(), String.valueOf(delay));
            pWorld.getPlayer().sendTitle(title, subTitle);
            // int fadeIn = text.getFadeIn();
            // int stay = text.getStay();
            // int fadeOut = text.getFadeOut();
            // player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
            // pWorld.getPlayer().sendTitle(title, subTitle);
        }
        run = Bukkit.getScheduler().scheduleSyncDelayedTask(pl, run(sendi, this), delay * 2 * 10);
        //Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, run(sendi,  this), 0, 10);
        if (cancelOnMove || cancelOnDamage)
            Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(PlayerMoveEvent e) {
        if (cancelOnMove)
            if (e.getPlayer().equals(pWorld.getPlayer()) &&
                (e.getTo() != null &&
                        (e.getTo().getX() != e.getFrom().getX() ||
                        e.getTo().getY() != e.getFrom().getY() ||
                        e.getTo().getZ() != e.getFrom().getZ()))
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
            getPl().getText().getMoved(pWorld.getPlayer());
            getPl().getEco().unCharge(pWorld.getPlayer(), pWorld.getPrice());
            getPl().getCmd().cooldowns.remove(pWorld.getPlayer().getUniqueId());
            getPl().getCmd().rtping.put(pWorld.getPlayer().getUniqueId(), false);
        }
    }

    private Runnable run(final CommandSender sendi, final Delay cls) {
        return () -> {
                HandlerList.unregisterAll(cls);
                if (getPl().getCmd().rtping.containsKey(pWorld.getPlayer().getUniqueId())) {
                    try {
                        getPl().getRTP().tp(sendi, pWorld);
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
