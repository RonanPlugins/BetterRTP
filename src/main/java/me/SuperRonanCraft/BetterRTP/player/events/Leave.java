package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.event.player.PlayerQuitEvent;

class Leave {

    void event(PlayerQuitEvent e) {
        BetterRTP.getInstance().getCmd().rtping.remove(e.getPlayer().getUniqueId());
    }
}
