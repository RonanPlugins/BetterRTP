package me.SuperRonanCraft.BetterRTPAddons;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;

public enum PermissionNodeAddon implements PermissionCheck {

    PORTALS("addon.portals"),
    MAGICSTICK("addon.magicstick"),
    PARTY("addon.party"),
    ;

    @Getter private final String node;

    PermissionNodeAddon(String node) {
        this.node = PermissionCheck.getPrefix() + node;
    }

    @Override public boolean isDev() {
        return false;
    }

}