package me.SuperRonanCraft.BetterRTPAddons;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo {

    private final Player player;
    private final Location location;

    public PlayerInfo(Player player, Location location) {
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
