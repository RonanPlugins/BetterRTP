package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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
            //Find a queue'd  location
            RTP_FindLocationEvent event = new RTP_FindLocationEvent(p, pWorld); //Find a queue'd location
            Bukkit.getServer().getPluginManager().callEvent(event);
            Location loc;
            if (event.getLocation() != null && pWorld.checkIsValid(event.getLocation()))
                loc = event.getLocation();
            else
                loc = pWorld.generateLocation();
            attempts++; //Add an attempt
            //Load chunk and find out if safe location (asynchronously)
            CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(loc);
            chunk.thenAccept(result -> {
                //BetterRTP.debug("Checking location for " + p.getName());
                Location tpLoc;
                float yaw = p.getLocation().getYaw();
                float pitch = p.getLocation().getPitch();
                switch (pWorld.getWorldtype()) { //Get a Y position and check for bad blocks
                    case NETHER:
                        tpLoc = getLocAtNether(loc.getBlockX(), loc.getBlockZ(), pWorld.getWorld(), yaw, pitch, pWorld); break;
                    case NORMAL:
                    default:
                        tpLoc = getLocAtNormal(loc.getBlockX(), loc.getBlockZ(), pWorld.getWorld(), yaw, pitch, pWorld);
                }
                attemptedLocations.add(loc);
                //Valid location?
                if (tpLoc != null && checkDepends(tpLoc)) {
                    if (getPl().getEco().charge(p, pWorld))
                        settings.teleport.sendPlayer(sendi, p, tpLoc, pWorld.getPrice(), attempts, type, pWorld.getWorldtype());
                } else
                    randomlyTeleport(sendi);
            });
        }
    }

    // Compressed code for MaxAttempts being met
    private void metMax(CommandSender sendi, Player p) {
        settings.teleport.failedTeleport(p, sendi);
        /*if (p == sendi)
            getPl().getText().getFailedNotSafe(sendi, settings.maxAttempts);
        else
            getPl().getText().getOtherNotSafe(sendi, settings.maxAttempts, p.getName());*/
        getPl().getCooldowns().removeCooldown(p);
        //getPl().getEco().unCharge(p, pWorld);
        getPl().getpInfo().getRtping().put(p, false);
    }

    private Location getLocAtNormal(int x, int z, World world, Float yaw, Float pitch, WorldPlayer pWorld) {
        Block b = world.getHighestBlockAt(x, z);
        if (b.getType().toString().endsWith("AIR")) //1.15.1 or less
            b = world.getBlockAt(x, b.getY() - 1, z);
        else if (!b.getType().isSolid()) { //Water, lava, shrubs...
            if (!badBlock(b.getType().name(), x, z, pWorld.getWorld(), null)) { //Make sure it's not an invalid block (ex: water, lava...)
                //int y = world.getHighestBlockYAt(x, z);
                b = world.getBlockAt(x, b.getY() - 1, z);
            }
        }
        //System.out.println(b.getType().name());
        if (b.getY() > pWorld.getMinY() && !badBlock(b.getType().name(), x, z, pWorld.getWorld(), pWorld.getBiomes())) {
            return new Location(world, (x + 0.5), b.getY() + 1, (z + 0.5), yaw, pitch);
        }
        return null;
    }

    private Location getLocAtNether(int x, int z, World world, Float yaw, Float pitch, WorldPlayer pWorld) {
        //System.out.println("-----------");
        for (int y = 1; y < world.getMaxHeight(); y++) {
           // System.out.println("--");
            Block block_current = world.getBlockAt(x, y, z);
            //System.out.println(block_current.getType().name());
            if (block_current.getType().name().endsWith("AIR") || !block_current.getType().isSolid()) {
                //System.out.println(block_current.getType().name());
                if (!block_current.getType().name().endsWith("AIR") &&
                        !block_current.getType().isSolid()) { //Block is not a solid (ex: lava, water...)
                    String block_in = block_current.getType().name();
                    if (badBlock(block_in, x, z, pWorld.getWorld(), null))
                        continue;//return null;
                }
                //System.out.println(block_current.getType().name());
                String block = world.getBlockAt(x, y - 1, z).getType().name();
                if (block.endsWith("AIR")) //Block below is air, skip
                    continue;
                if (world.getBlockAt(x, y + 1, z).getType().name().endsWith("AIR") //Head space
                        && !badBlock(block, x, z, pWorld.getWorld(), pWorld.getBiomes())) //Valid block
                    return new Location(world, (x + 0.5), y, (z + 0.5), yaw, pitch);
            }
        }
        return null;
    }

    private boolean checkDepends(Location loc) {
        return settings.softDepends.checkLocation(loc);
    }

    // Bad blocks, or bad biome
    private boolean badBlock(String block, int x, int z, World world, List<String> biomes) {
        for (String currentBlock : settings.blockList) //Check Block
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