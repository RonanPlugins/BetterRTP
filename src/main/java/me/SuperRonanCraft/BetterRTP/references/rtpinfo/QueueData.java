package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import org.bukkit.Location;

public class QueueData {

    @Getter Location location;
    @Getter long generated;

    public QueueData(Location location, long generated) {
        this.location = location;
        this.generated = generated;
    }

}
