package me.SuperRonanCraft.BetterRTP.references.customEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RTP_TeleportPreEvent extends RTPEvent { //Called upon every rtp call, does not mean player will be teleported

    Player p;

    public RTP_TeleportPreEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

}
