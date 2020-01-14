package me.SuperRonanCraft.BetterRTP.player;

import me.SuperRonanCraft.BetterRTP.references.worlds.PlayerWorld;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

class Delay implements Listener {
    private int run;
    private PlayerWorld pWorld;
    private Main pl = Main.getInstance();

    Delay(CommandSender sendi, PlayerWorld pWorld, int delay, boolean cancelOnMove) {
        this.pWorld = pWorld;
        delay(sendi, delay, cancelOnMove);
    }

    @SuppressWarnings("deprecation")
    private void delay(CommandSender sendi, int delay, boolean cancelOnMove) {
        Main pl = Main.getInstance();
        if (sendi.equals(pWorld.getPlayer()) && delay != 0 && pl.getText().getTitleDelayChat())
            pl.getText().getDelay(sendi, String.valueOf(delay));
        if (pl.getText().getSoundsEnabled()) {
            Sound sound = pl.getText().getSoundsDelay();
            if (sound != null)
                pWorld.getPlayer().playSound(pWorld.getPlayer().getLocation(), sound, 1F, 1F);
        }
        if (pl.getText().getTitleEnabled()) {
            String title = pl.getText().getTitleDelay(pWorld.getPlayer().getName(), String.valueOf(delay));
            String subTitle = pl.getText().getSubTitleDelay(pWorld.getPlayer().getName(), String.valueOf(delay));
            pWorld.getPlayer().sendTitle(title, subTitle);
            // int fadeIn = text.getFadeIn();
            // int stay = text.getStay();
            // int fadeOut = text.getFadeOut();
            // player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
            // pWorld.getPlayer().sendTitle(title, subTitle);
        }
        run = Bukkit.getScheduler().scheduleSyncDelayedTask(pl, run(sendi, this), delay * 2 * 10);
        //Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, run(sendi,  this), 0, 10);
        if (cancelOnMove)
            Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(PlayerMoveEvent e) {
        if (e.getPlayer().equals(pWorld.getPlayer())) {
            Bukkit.getScheduler().cancelTask(run);
            if (!Bukkit.getScheduler().isCurrentlyRunning(run)) {
                HandlerList.unregisterAll(this);
                pl.getText().getMoved(pWorld.getPlayer());
                pl.getEco().unCharge(pWorld.getPlayer(), pWorld.getPrice());
                pl.getCmd().cooldowns.remove(pWorld.getPlayer().getUniqueId());
                pl.getCmd().rtping.put(pWorld.getPlayer().getUniqueId(), false);
            }
        }
    }

    private Runnable run(final CommandSender sendi, final Delay cls) {
        return () -> {
                HandlerList.unregisterAll(cls);
                if (pl.getCmd().rtping.containsKey(pWorld.getPlayer().getUniqueId())) {
                    try {
                        pl.getRTP().tp(sendi, pWorld);
                    } catch (NullPointerException e) {
                        if (pWorld.getPrice() > 0)
                            pl.getEco().unCharge(pWorld.getPlayer(), pWorld.getPrice());
                    }
                    pl.getCmd().rtping.put(pWorld.getPlayer().getUniqueId(), false);
                } else if (pWorld.getPrice() > 0)
                    pl.getEco().unCharge(pWorld.getPlayer(), pWorld.getPrice());
                Bukkit.getScheduler().cancelTask(run);
        };
    }
}
