package me.SuperRonanCraft.BetterRTP.references.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class RTP_CancelledEvent extends RTPEvent { //Called when a delayed rtp is cancelled cause player moved

    Player p;
    private static final HandlerList handler = new HandlerList();

    public RTP_CancelledEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }
}
