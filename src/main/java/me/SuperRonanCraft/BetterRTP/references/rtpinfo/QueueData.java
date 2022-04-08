package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import org.bukkit.Location;

public class QueueData {

    //@Getter final String identifier;
    @Getter @Setter Location location;
    @Getter final long generated;

    public QueueData(Location location, long generated/*, String identifier*/) {
        this.location = location;
        this.generated = generated;
        //this.identifier = identifier;
    }

    public QueueData(RTPWorld rtpWorld) {
        this.location = WorldPlayer.generateLocation(rtpWorld);
        this.generated = System.currentTimeMillis();
        //this.identifier = rtpWorld.getID();
    }

}
