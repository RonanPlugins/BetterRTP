package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class CooldownData {

    @Getter private final UUID uuid;
    @Getter @Setter private Long time;
    @Getter private final @Nullable World world;

    public CooldownData(UUID uuid, Long time, @Nullable World world) {
        this.uuid = uuid;
        this.time = time;
        this.world = world;
    }
}
