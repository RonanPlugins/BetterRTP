package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;

public enum RTP_ERROR_REQUEST_REASON {
    IS_RTPING(MessagesCore.ALREADY),
    NO_PERMISSION(MessagesCore.NOPERMISSION),
    WORLD_DISABLED(MessagesCore.DISABLED_WORLD),
    COOLDOWN(MessagesCore.COOLDOWN),
    PRICE_ECONOMY(MessagesCore.FAILED_PRICE),
    PRICE_HUNGER(MessagesCore.FAILED_HUNGER);

    @Getter private final MessagesCore msg;

    RTP_ERROR_REQUEST_REASON(MessagesCore msg) {
        this.msg = msg;
    }
}
