package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.FoliaHelper;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.RandomLocation;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Bukkit;

public class DatabaseHandler {

    @Getter private final DatabasePlayers databasePlayers = new DatabasePlayers();
    @Getter private final DatabaseCooldowns databaseCooldowns = new DatabaseCooldowns();
    @Getter private final DatabaseQueue databaseQueue = new DatabaseQueue();
    @Getter private final DatabaseChunkData databaseChunks = new DatabaseChunkData();

    public void load() {
        AsyncHandler.async(() -> {
            databasePlayers.load();
            databaseCooldowns.load();
            databaseQueue.load();
            databaseChunks.load();
        });
    }

    public static DatabasePlayers getPlayers() {
        return BetterRTP.getInstance().getDatabaseHandler().getDatabasePlayers();
    }

    public static DatabaseCooldowns getCooldowns() {
        return BetterRTP.getInstance().getDatabaseHandler().getDatabaseCooldowns();
    }

    public static DatabaseQueue getQueue() {
        return BetterRTP.getInstance().getDatabaseHandler().getDatabaseQueue();
    }

    //public static DatabaseChunkData getChunks() {
    //    return BetterRTP.getInstance().getDatabaseHandler().getDatabaseChunks();
    //}

}
