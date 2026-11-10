package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Damage {
    static boolean canCancel(EntityDamageEvent.DamageCause damageCause) {
        return true; // TODO: Allow for filtering damage causes
    }

    static boolean isInInvincibleMode(Player player) {
        return HelperPlayer.getData(player).getInvincibleEndTime() > System.currentTimeMillis();
    }

    static void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;

        if (!canCancel(event.getCause())) return;
        if (!isInInvincibleMode(player)) return;

        event.setCancelled(true);
    }
}
