package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;

public class ChunkDatabaseCache {
    private @Getter Chunk chunk;
    private @Getter int maxy;
    private @Getter Biome biome;

    public ChunkDatabaseCache(Chunk chunk, int maxy, Biome biome) {
        this.chunk = chunk;
        this.maxy = maxy;
        this.biome = biome;
    }
}
