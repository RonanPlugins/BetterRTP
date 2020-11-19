package me.SuperRonanCraft.BetterRTP.references.customEvents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RTP_TeleportEvent extends Event {

    Player p;
    Location loc;
    private static final HandlerList handler = new HandlerList();

    public RTP_TeleportEvent(Player p, Location loc) {
        this.p = p;
        this.loc = loc;
    }

    public Player getPlayer() {
        return p;
    }

    public Location getLocation() {
        return loc;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    public void changeLocation(Location loc) {
        this.loc = loc;
    }
}
