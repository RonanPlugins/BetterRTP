package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import lombok.Setter;

public class RTP_PlayerInfo {

    @Getter @Setter boolean
            applyDelay,
            applyCooldown,
            checkCooldown,
            takeMoney,
            takeHunger;

    public RTP_PlayerInfo(boolean applyDelay, boolean applyCooldown) {
        this.applyDelay = applyDelay;
        this.applyCooldown = applyCooldown;
    }

    public RTP_PlayerInfo(boolean applyDelay,
                          boolean applyCooldown,
                          boolean checkCooldown,
                          boolean takeMoney,
                          boolean takeHunger) {
        this.applyDelay = applyDelay;
        this.applyCooldown = applyCooldown;
        this.checkCooldown = checkCooldown;
        this.takeMoney = takeMoney;
        this.takeHunger = takeHunger;
    }

}
