package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.systems.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.systems.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Teleport {

    static void tpEvent(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        PlayerData data = HelperPlayer.getData(p);
    }
}
