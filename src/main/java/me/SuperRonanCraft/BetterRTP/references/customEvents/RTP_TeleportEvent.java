package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.references.worlds.WORLD_TYPE;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RTP_TeleportEvent extends Event implements RTPEvent {

    Player p;
    Location loc;
    WORLD_TYPE worldType;
    private static final HandlerList handler = new HandlerList();

    public RTP_TeleportEvent(Player p, Location loc, WORLD_TYPE worldType) {
        this.p = p;
        this.loc = loc;
        this.worldType = worldType;
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

    public WORLD_TYPE getWorldType() {
        return worldType;
    }
}
