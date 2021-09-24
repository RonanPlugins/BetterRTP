package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

class Leave {

    void event(PlayerQuitEvent e) {
        BetterRTP pl = BetterRTP.getInstance();
        UUID id = e.getPlayer().getUniqueId();
        pl.getCmd().rtping.remove(id);
        pl.getCmd().cooldowns.unloadPlayer(id);
    }
}
