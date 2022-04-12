package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

class Leave {

    static void event(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        HelperPlayer.unload(p);
    }
}
