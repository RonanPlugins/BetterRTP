package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;

public class DatabaseHandler {

    @Getter private final DatabasePlayers databasePlayers = new DatabasePlayers();
    @Getter private final DatabaseCooldownsWorlds databaseCooldownsWorlds = new DatabaseCooldownsWorlds();
    @Getter private final DatabaseQueue databaseQueue = new DatabaseQueue();

    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            databasePlayers.load();
            databaseCooldownsWorlds.load();
            databaseQueue.load();
        });
    }

    public static DatabasePlayers getPlayers() {
        return BetterRTP.getInstance().getDatabaseHandler().getDatabasePlayers();
    }

    public static DatabaseCooldownsWorlds getWorldCooldowns() {
        return BetterRTP.getInstance().getDatabaseHandler().getDatabaseCooldownsWorlds();
    }

    public static DatabaseQueue getQueue() {
        return BetterRTP.getInstance().getDatabaseHandler().getDatabaseQueue();
    }

}
