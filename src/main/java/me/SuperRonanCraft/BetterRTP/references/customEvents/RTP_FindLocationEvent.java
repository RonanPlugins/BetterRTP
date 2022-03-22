package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

//Called when an rtp has found a valid location
public class RTP_FindLocationEvent extends RTPEvent {

    Player p;
    RTPWorld world;
    Location loc = null;

    public RTP_FindLocationEvent(Player p, RTPWorld world) {
        this.p = p;
        this.world = world;
    }

    //A location can be pushed in if a developer wants to inject a custom location
    //Safe location code will still be run!
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
