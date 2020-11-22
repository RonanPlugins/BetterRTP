package me.SuperRonanCraft.BetterRTP.references.customEvents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RTP_CancelledEvent extends Event {

    Player p;
    private static final HandlerList handler = new HandlerList();

    public RTP_CancelledEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }
}
