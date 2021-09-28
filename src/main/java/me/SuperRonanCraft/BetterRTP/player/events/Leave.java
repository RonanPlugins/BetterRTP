package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

class Leave {

    void event(PlayerQuitEvent e) {
        BetterRTP pl = BetterRTP.getInstance();
        Player p = e.getPlayer();
        pl.getpInfo().getRtping().remove(p);
        pl.getpInfo().unload(p);
    }
}
