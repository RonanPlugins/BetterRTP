package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WORLD_TYPE;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RTP_TeleportEvent extends RTPEvent {

    Player p;
    Location loc;
    WORLD_TYPE worldType;

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

    public void changeLocation(Location loc) {
        this.loc = loc;
    }

    public WORLD_TYPE getWorldType() {
        return worldType;
    }
}
