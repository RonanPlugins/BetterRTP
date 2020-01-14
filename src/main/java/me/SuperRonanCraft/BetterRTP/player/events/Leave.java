package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.event.player.PlayerQuitEvent;

class Leave {

    @SuppressWarnings("unchecked")
    void event(PlayerQuitEvent e) {
        if (Main.getInstance().getCmd().rtping.containsKey(e.getPlayer().getUniqueId()))
            Main.getInstance().getCmd().rtping.remove(e.getPlayer().getUniqueId());
    }
}
