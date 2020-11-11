package me.SuperRonanCraft.BetterRTP.references.invs;

import me.SuperRonanCraft.BetterRTP.references.invs.enums.RTPInventory;
import me.SuperRonanCraft.BetterRTP.references.invs.types.RTPInvBlacklist;
import me.SuperRonanCraft.BetterRTP.references.invs.types.RTPInvCoordinates;
import me.SuperRonanCraft.BetterRTP.references.invs.types.RTPInvMain;
import me.SuperRonanCraft.BetterRTP.references.invs.types.RTPInvWorlds;

public enum RTP_INV_SETTINGS {
    MAIN(new RTPInvMain(), false),
    BLACKLIST(new RTPInvBlacklist(), true),
    COORDINATES(new RTPInvCoordinates(), true),
    WORLDS(new RTPInvWorlds(), false);

    private RTPInventory inv;
    private boolean showInMain;

    RTP_INV_SETTINGS(RTPInventory inv, boolean showInMain) {
        this.inv = inv;
        this.showInMain = showInMain;
    }

    public RTPInventory getInv() {
        return inv;
    }

    public Boolean getShowMain() {
        return showInMain;
    }

    void load(RTP_INV_SETTINGS type) {
        inv.load(type);
    }
}
