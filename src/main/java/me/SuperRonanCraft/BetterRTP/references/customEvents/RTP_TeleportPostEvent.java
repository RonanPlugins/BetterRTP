package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RTP_TeleportPostEvent extends RTPEvent {

    Player p;
    Location loc;
    Location oldLoc;
    RTP_TYPE type;

    public RTP_TeleportPostEvent(Player p, Location loc, Location oldLoc, RTP_TYPE type) {
        this.p = p;
        this.loc = loc;
        this.oldLoc = oldLoc;
        this.type = type;
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

    public RTP_TYPE getType() {
        return type;
    }
}
