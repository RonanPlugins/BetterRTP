package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

class Leave {

    static void event(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        try {
            BetterRTP.getInstance().getRTP().getOperations().cancel(p.getUniqueId(), p);
        } finally {
            HelperPlayer.unload(p);
        }
    }
}
