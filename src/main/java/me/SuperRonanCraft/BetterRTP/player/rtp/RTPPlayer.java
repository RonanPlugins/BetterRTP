package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WORLD_TYPE;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RTPPlayer {

    private final Player p;
    private final RTP settings;
    WorldPlayer pWorld;
    RTP_TYPE type;
    int attempts;
    List<Location> attemptedLocations = new ArrayList<>();

    RTPPlayer(Player p, RTP settings, WorldPlayer pWorld, RTP_TYPE type) {
        this.p = p;
        this.settings = settings;
        this.pWorld = pWorld;
        this.type = type;
    }

    public Player getPlayer() {
        return p;
    }

    void randomlyTeleport(CommandSender sendi) {
        if (attempts >= settings.maxAttempts) //Cancel out, too many tries
            metMax(sendi, p);
        else { //Try again to find a safe location
            //Find a location from another Plugin
            RTP_FindLocationEvent event = new RTP_FindLocationEvent(p, pWorld); //Find an external plugin location
            Bukkit.getServer().getPluginManager().callEvent(event);
            //Async Location finder
            Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
                Location loc;
                if (event.getLocation() != null) // && WorldPlayer.checkIsValid(event.getLocation(), pWorld))
                    loc = event.getLocation();
                else {
                    QueueData queueData = QueueHandler.getRandomAsync(pWorld);
                    if (queueData != null)
                        loc = queueData.getLocation();
                    else
                        loc = WorldPlayer.generateLocation(pWorld);
                }
                attempts++; //Add an attempt
                //Load chunk and find out if safe location (asynchronously)
                CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(loc);
                chunk.thenAccept(result -> {
                    //BetterRTP.debug("Checking location for " + p.getName());
                    Location tpLoc;
                    tpLoc = getSafeLocation(pWorld.getWorldtype(), pWorld.getWorld(), loc, pWorld.getMinY(), pWorld.getMaxY(), pWorld.getBiomes());
                    attemptedLocations.add(loc);
                    //Valid location?
                    if (tpLoc != null && checkDepends(tpLoc)) {
                        tpLoc.add(0.5, 0, 0.5); //Center location
                        if (getPl().getEco().charge(p, pWorld)) {
                            tpLoc.setYaw(p.getLocation().getYaw());
                            tpLoc.setPitch(p.getLocation().getPitch());
                            Bukkit.getScheduler().runTask(BetterRTP.getInstance(), () ->
                                settings.teleport.sendPlayer(sendi, p, tpLoc, pWorld.getPrice(), attempts, type, pWorld.getWorldtype()));
                        }
                    } else {
                        randomlyTeleport(sendi);
                        QueueHandler.remove(loc);
                    }
                });
            });
        }
    }

    public static Location getSafeLocation(WORLD_TYPE type, World world, Location loc, int minY, int maxY, List<String> biomes) {
        switch (type) { //Get a Y position and check for bad blocks
            case NETHER: return getLocAtNether(loc.getBlockX(), loc.getBlockZ(), minY, maxY, world, biomes);
            case NORMAL:
            default: return getLocAtNormal(loc.getBlockX(), loc.getBlockZ(), minY, maxY, world, biomes);
        }
    }

    // Compressed code for MaxAttempts being met
    private void metMax(CommandSender sendi, Player p) {
        settings.teleport.failedTeleport(p, sendi);
        getPl().getCooldowns().removeCooldown(p, pWorld.getWorld());
        getPl().getpInfo().getRtping().put(p, false);
    }

    private static Location getLocAtNormal(int x, int z, int minY, int maxY, World world, List<String> biomes) {
        Block b = world.getHighestBlockAt(x, z);
        if (b.getType().toString().endsWith("AIR")) //1.15.1 or less
            b = world.getBlockAt(x, b.getY() - 1, z);
        else if (!b.getType().isSolid()) { //Water, lava, shrubs...
            if (!badBlock(b.getType().name(), x, z, world, null)) { //Make sure it's not an invalid block (ex: water, lava...)
                //int y = world.getHighestBlockYAt(x, z);
                b = world.getBlockAt(x, b.getY() - 1, z);
            }
        }
        //Between max and min y
        if (    b.getY() >= minY
                && b.getY() <= maxY
                && !badBlock(b.getType().name(), x, z, world, biomes)) {
            return new Location(world, x, b.getY() + 1, z);
        }
        return null;
    }

    private static Location getLocAtNether(int x, int z, int minY, int maxY, World world, List<String> biomes) {
        //Max and Min Y
        for (int y = minY + 1; y < maxY/*world.getMaxHeight()*/; y++) {
            Block block_current = world.getBlockAt(x, y, z);
            if (block_current.getType().name().endsWith("AIR") || !block_current.getType().isSolid()) {
                if (!block_current.getType().name().endsWith("AIR") &&
                        !block_current.getType().isSolid()) { //Block is not a solid (ex: lava, water...)
                    String block_in = block_current.getType().name();
                    if (badBlock(block_in, x, z, world, null))
                        continue;
                }
                String block = world.getBlockAt(x, y - 1, z).getType().name();
                if (block.endsWith("AIR")) //Block below is air, skip
                    continue;
                if (world.getBlockAt(x, y + 1, z).getType().name().endsWith("AIR") //Head space
                        && !badBlock(block, x, z, world, biomes)) //Valid block
                    return new Location(world, x, y, z);
            }
        }
        return null;
    }

    public static boolean checkDepends(Location loc) {
        return RTPPluginValidation.checkLocation(loc);
    }

    // Bad blocks, or bad biome
    public static boolean badBlock(String block, int x, int z, World world, List<String> biomes) {
        for (String currentBlock : BetterRTP.getInstance().getRTP().blockList) //Check Block
            if (currentBlock.toUpperCase().equals(block))
                return true;
        //Check Biomes
        if (biomes == null || biomes.isEmpty())
            return false;
        String biomeCurrent = world.getBiome(x, z).name();
        for (String biome : biomes)
            if (biomeCurrent.toUpperCase().contains(biome.toUpperCase()))
                return false;
        return true;
        //FALSE MEANS NO BAD BLOCKS/BIOME WHERE FOUND!
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}