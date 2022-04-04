package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

import java.util.UUID;

public class CooldownData {

    @Getter private final UUID uuid;
    @Getter @Setter private Long time;
    @Getter private final World world;

    public CooldownData(UUID uuid, Long time, World world) {
        this.uuid = uuid;
        this.time = time;
        this.world = world;
    }
}
