package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class CooldownData {

    @Getter private final UUID uuid;
    @Getter @Setter private Long time;

    public CooldownData(UUID uuid, Long time) {
        this.uuid = uuid;
        this.time = time;
    }
}
