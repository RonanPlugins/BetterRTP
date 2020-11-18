package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlashbackPlayerInfo {

    private final Player player;
    private final Location location;

    public FlashbackPlayerInfo(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }
}
