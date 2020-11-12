package me.SuperRonanCraft.BetterRTP.references.customEvents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RTP_TeleportPostEvent extends Event {

    Player p;
    Location loc;
    Location oldLoc;
    private static final HandlerList handler = new HandlerList();

    public RTP_TeleportPostEvent(Player p, Location loc, Location oldLoc) {
        this.p = p;
        this.loc = loc;
        this.oldLoc = oldLoc;
    }

    public Player getPlayer() {
        return p;
    }

    public Location getLocation() {
        return loc;
    }

    public Location getOldLocation() {
        return oldLoc;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }
}
