package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.Getter;
import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueGenerator;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class DatabaseChunkData extends SQLite {

    public DatabaseChunkData() {
        super(DATABASE_TYPE.CHUNK_DATA);
    }

    @Override
    public List<String> getTables() {
        List<String> list = new ArrayList<>();
        list.add("ChunkData");
        return list;
    }

    public enum COLUMNS {
        ID("id", "integer PRIMARY KEY AUTOINCREMENT"),
        //Chunk Data
        WORLD("world", "varchar(32)"),
        X("x", "long"),
        Z("z", "long"),
        BIOME("biome", "string"),
        MAX_Y("max_y", "integer"),
        ;

        public final String name;
        public final String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }


    public void addChunk(List<ChunkDatabaseCache> cacheList) {
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            String pre = "INSERT OR REPLACE INTO ";
            List<String> sqls = new ArrayList<>();
            List<List<Object>> params = new ArrayList<>();
            for (ChunkDatabaseCache cache : cacheList) {
                sqls.add(pre + tables.get(0) + " ("
                        + COLUMNS.WORLD.name + ", "
                        + COLUMNS.X.name + ", "
                        + COLUMNS.Z.name + ", "
                        + COLUMNS.BIOME.name + ", "
                        + COLUMNS.MAX_Y.name + " "
                        + ") VALUES(?, ?, ?, ?, ?)");
                params.add(new ArrayList<Object>() {{
                    add(cache.getChunk().getWorld().getName());
                    add(cache.getChunk().getX());
                    add(cache.getChunk().getZ());
                    add(cache.getBiome().name());
                    add(cache.getMaxy());
                }});
            }
            sqlUpdate(sqls, params);
        });
    }

}