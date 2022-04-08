package me.SuperRonanCraft.BetterRTP.references.customEvents;

import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

//Called when an rtp is finding a valid location
public class RTP_FindLocationEvent extends RTPEvent {

    Player p;
    RTPWorld world;
    Location loc; //Used to force a location into find event

    public RTP_FindLocationEvent(Player p, RTPWorld world) {
        this.p = p;
        this.world = world;
    }

    //A location can be pushed in if a developer wants to inject a custom location
    //Safe block code will still be run!
    public void setLocation(Location loc) {
        this.loc = loc;
    }

    @Nullable
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
