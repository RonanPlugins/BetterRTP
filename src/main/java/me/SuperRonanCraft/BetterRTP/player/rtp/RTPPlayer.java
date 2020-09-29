package me.SuperRonanCraft.BetterRTP.player.rtp;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RTPPlayer {

    private final Player p;
    private final RTP settings;

    RTPPlayer(Player p, RTP settings) {
        this.p = p;
        this.settings = settings;
    }

    public void teleport(CommandSender sendi, String worldName, List<String> biomes, boolean delay) {
        // Check overrides
        if (worldName == null)
            worldName = p.getWorld().getName();
        if (settings.overriden.containsKey(worldName))
            worldName = settings.overriden.get(worldName);
        // Not forced and has 'betterrtp.world.<world>'
        if (sendi == p && !getPl().getPerms().getAWorld(sendi, worldName)) {
            //getPl().getCmd().cooldowns.remove(p.getUniqueId());
            getPl().getText().getNoPermissionWorld(p, worldName);
            return;
        }
        // Check disabled worlds
        if (settings.disabledWorlds.contains(worldName)) {
            getPl().getText().getDisabledWorld(sendi, worldName);
            //getPl().getCmd().cooldowns.remove(p.getUniqueId());
            return;
        }
        // Check if nulled or world doesnt exist
        if (Bukkit.getWorld(worldName) == null) {
            getPl().getText().getNotExist(sendi, worldName);
            //getPl().getCmd().cooldowns.remove(p.getUniqueId());
            return;
        }
        WorldPlayer pWorld = settings.getPlayerWorld(p, worldName, biomes, true);
        // Economy
        if (!getPl().getEco().charge(p, pWorld.getPrice())) {
            //getPl().getCmd().cooldowns.remove(p.getUniqueId());
            return;
        }
        //Cooldown
        getPl().getCmd().cooldowns.add(p.getUniqueId());
        // Delaying? Else, just go
        getPl().getCmd().rtping.put(p.getUniqueId(), true); //Cache player so they cant run '/rtp' again while rtp'ing
        if (getPl().getSettings().delayEnabled && delay) {
            new RTPDelay(sendi, this, pWorld, settings.delayTime, settings.cancelOnMove, settings.cancelOnDamage);
        } else {
            settings.teleport.beforeTeleportInstant(p);
            findSafeLocation(sendi, pWorld);
        }
    }

    void findSafeLocation(CommandSender sendi, WorldPlayer pWorld) {
        if (pWorld.getAttempts() >= settings.maxAttempts) //Cancel out, too many tried
            metMax(sendi, pWorld.getPlayer(), pWorld.getPrice());
        else { //Try again to find a safe location
            Location loc = pWorld.generateRandomXZ(settings.defaultWorld); //randomLoc(pWorld);
            CompletableFuture<Chunk> chunk = PaperLib.getChunkAtAsync(pWorld.getWorld(), loc.getBlockX(), loc.getBlockZ());
            chunk.thenAccept(result -> {
                Location tpLoc;
                float yaw = pWorld.getPlayer().getLocation().getYaw();
                float pitch = pWorld.getPlayer().getLocation().getPitch();
                switch (pWorld.getWorldtype()) { //Get a Y position and check for bad blocks
                    case NETHER:
                        tpLoc = getLocAtNether(loc.getBlockX(), loc.getBlockZ(), pWorld.getWorld(), yaw, pitch, pWorld); break;
                    case NORMAL:
                    default:
                        tpLoc = getLocAtNormal(loc.getBlockX(), loc.getBlockZ(), pWorld.getWorld(), yaw, pitch, pWorld);
                }
                if (tpLoc != null && checkDepends(tpLoc))
                    settings.teleport.sendPlayer(sendi, pWorld.getPlayer(), tpLoc, pWorld.getPrice(), pWorld.getAttempts());
                else
                    findSafeLocation(sendi, pWorld);
            });
        }
    }

    // Compressed code for MaxAttempts being met
    private void metMax(CommandSender sendi, Player p, int price) {
        if (p == sendi)
            getPl().getText().getFailedNotSafe(sendi, settings.maxAttempts);
        else
            getPl().getText().getOtherNotSafe(sendi, settings.maxAttempts, p.getDisplayName());
        getPl().getCmd().cooldowns.remove(p.getUniqueId());
        getPl().getEco().unCharge(p, price);
        getPl().getCmd().rtping.put(p.getUniqueId(), false);
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
        if (b.getY() > 0 && !badBlock(b.getType().name(), x, z, pWorld.getWorld(), pWorld.getBiomes())) {
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

    private Main getPl() {
        return Main.getInstance();
    }
}