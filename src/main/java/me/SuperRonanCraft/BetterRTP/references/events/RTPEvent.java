package me.SuperRonanCraft.BetterRTP.references.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RTPEvent extends Event {

    private static final HandlerList handler = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }
}
