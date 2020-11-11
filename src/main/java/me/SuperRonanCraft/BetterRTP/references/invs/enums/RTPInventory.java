package me.SuperRonanCraft.BetterRTP.references.invs.enums;

import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;

public abstract class RTPInventory implements RTPInventory_Defaults {

    public RTP_INV_SETTINGS type;

    public void load(RTP_INV_SETTINGS type) {
        this.type = type;
    }
}
