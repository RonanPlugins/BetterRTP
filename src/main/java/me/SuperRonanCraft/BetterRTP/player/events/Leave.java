package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

class Leave {

    static void event(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        HelperPlayer.unload(p);
        // Other quit listeners can still request data after ours, clear again next tick
        AsyncHandler.syncLater(() -> {
            if (!p.isOnline())
                HelperPlayer.unload(p);
        }, 1L);
    }
}
