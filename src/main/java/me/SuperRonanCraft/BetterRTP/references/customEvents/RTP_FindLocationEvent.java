package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.references.worlds.RTPWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RTP_FindLocationEvent {

    Player p;
    RTPWorld world;
    Location loc = null;

    public RTP_FindLocationEvent(Player p, RTPWorld world) {
        this.p = p;
        this.world = world;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public Location getLocation() {
        return loc;
    }

    public RTPWorld getWorld() {
        return world;
    }

    public Player getPlayer() {
        return p;
    }
}
