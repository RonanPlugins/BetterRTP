package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class CooldownData {
    @Getter private final UUID uuid;
    @Getter @Setter private Long time;
    @Getter @Setter int uses;

    public CooldownData(UUID uuid, Long time, int uses) {
        this.uuid = uuid;
        this.time = time;
        this.uses = uses;
    }
}
