package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.event.player.PlayerQuitEvent;

class Leave {

    void event(PlayerQuitEvent e) {
        Main.getInstance().getCmd().rtping.remove(e.getPlayer().getUniqueId());
    }
}
