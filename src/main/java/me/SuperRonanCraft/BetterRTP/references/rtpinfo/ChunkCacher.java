package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.database.ChunkDatabaseCache;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import org.bukkit.*;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChunkCacher {

    List<ChunkDatabaseCache> cache = new ArrayList<>();

    public void runChunkTest() {
        BetterRTP.getInstance().getLogger().info("---------------- Starting chunk test!");
        World world = Bukkit.getWorld("world");
        cacheChunkAt(world, 32, -32, -32, -32);
    }

    private void upload() {
        BetterRTP.getInstance().getLogger().info("Uploading " + cache.size() + " chunk data's");
        DatabaseHandler.getChunks().addChunk(cache);
    }

    private void cacheTask(World world, int goal, int start, int xat, int zat) {
        zat += 1;
        if (zat > goal) {
            zat = start;
            xat += 1;
        }
        if (xat <= goal)
            cacheChunkAt(world, goal, start, xat, zat);
        else
            upload();
    }

    private void cacheChunkAt(World world, int goal, int start, int xat, int zat) {
        CompletableFuture<Chunk> task = PaperLib.getChunkAtAsync(new Location(world, xat * 16, 0, zat * 16));
        task.thenAccept(chunk -> {
            try {
                ChunkSnapshot snapshot = chunk.getChunkSnapshot(true, true, false);
                int maxy = snapshot.getHighestBlockYAt(8, 8);
                Biome biome = snapshot.getBiome(8, 8);
                //BetterRTP.getInstance().getLogger().info("Added " + chunk.getX() + " " + chunk.getZ());
                cache.add(new ChunkDatabaseCache(chunk, maxy, biome));
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException();
                //BetterRTP.getInstance().getLogger().info("Tried Adding " + chunk.getX() + " " + chunk.getZ());
            }
            chunk.unload();
        }).thenRun(() -> cacheTask(world, goal, start, xat, zat));
    }
}
