package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import org.bukkit.Location;

public class QueueData {

    @Getter final int database_id;
    @Getter @Setter Location location;
    @Getter final long generated;

    public QueueData(Location location, long generated, int database_id) {
        this.location = location;
        this.generated = generated;
        this.database_id = database_id;
    }

}
