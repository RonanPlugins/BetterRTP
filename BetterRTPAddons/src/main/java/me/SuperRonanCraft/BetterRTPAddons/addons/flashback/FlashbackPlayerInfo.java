package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlashbackPlayerInfo {

    private final Player player;
    private final Location location;
    private final Long time;

    public FlashbackPlayerInfo(Player player, Location location, Long time) {
        this.player = player;
        this.location = location;
        this.time = time;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public Long getTime() {
        return time;
    }
}
