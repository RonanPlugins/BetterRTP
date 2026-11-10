package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class CooldownData {

    @Getter private final UUID uuid;
    @Getter @Setter private Long time;

    public CooldownData(UUID uuid, Long time) {
        this.uuid = uuid;
        this.time = time;
    }
}
