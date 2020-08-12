package me.SuperRonanCraft.BetterRTP.player.rtp;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.worlds.*;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class RTP {

    private final RTPTeleport teleport = new RTPTeleport();
    //Cache
    public HashMap<String, RTPWorld> customWorlds = new HashMap<>();
    public HashMap<String, String> overriden = new HashMap<>();
    public Default defaultWorld = new Default();
    private Random rn = new Random();
    private List<String> disabledWorlds, blockList;
    private int maxAttempts, delayTime;
    private boolean cancelOnMove, cancelOnDamage;
    public HashMap<String, RTP_WORLD_TYPE> world_type = new HashMap<>();

    public RTPTeleport getTeleport() {
        return teleport;
    }

    public void load() {
        defaultWorld.setup();
        FileBasics.FILETYPE config = getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        disabledWorlds = config.getStringList("DisabledWorlds");
        maxAttempts = config.getInt("Settings.MaxAttempts");
        delayTime = config.getInt("Settings.Delay.Time");
        cancelOnMove = config.getBoolean("Settings.Delay.CancelOnMove");
        cancelOnDamage = config.getBoolean("Settings.Delay.CancelOnDamage");
        blockList = config.getStringList("BlacklistedBlocks");
        //OVER-RIDES
        try {
            overriden.clear();
            List<Map<?, ?>> override_map = config.getMapList("Overrides");
            for (Map<?, ?> m : override_map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    overriden.put(entry.getKey().toString(), entry.getValue().toString());
                    if (getPl().getSettings().debug)
                        getPl().getLogger().info("- Override '" + entry.getKey() + "' -> '" + entry.getValue() + "' added");
                }
            //for (String s : config.getConfigurationSection("Override").getKeys(false))
            //    overriden.put(s, config.getString("Override." + s));
        } catch (Exception e) {
            //No Overrides
        }

        //CUSTOM WORLDS
        try {
            customWorlds.clear();
            List<Map<?, ?>> map = config.getMapList("CustomWorlds");
            for (Map<?, ?> m : map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    customWorlds.put(entry.getKey().toString(), new Custom(entry.getKey().toString()));
                    if (getPl().getSettings().debug)
                        getPl().getLogger().info("- Custom World '" + entry.getKey() + "' registered");
                }
        } catch (Exception e) {
            //No Custom Worlds
        }

        try {
            world_type.clear();
            for (World world : Bukkit.getWorlds())
                world_type.put(world.getName(), RTP_WORLD_TYPE.NORMAL);
            List<Map<?, ?>> world_map = config.getMapList("WorldType");
            for (Map<?, ?> m : world_map)
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    if (world_type.containsKey(entry.getKey())) {
                        try {
                            RTP_WORLD_TYPE type = RTP_WORLD_TYPE.valueOf(entry.getValue().toString().toUpperCase());
                            world_type.put(entry.getKey().toString(), type);
                        } catch(IllegalArgumentException e) {
                            StringBuilder valids = new StringBuilder();
                            for (RTP_WORLD_TYPE type : RTP_WORLD_TYPE.values())
                                valids.append(type.name()).append(", ");
                            valids.replace(valids.length() - 2, valids.length(), "");
                            getPl().getLogger().severe("World Type for '" + entry.getKey() + "' is INVALID '" + entry.getValue() +
                                    "'. Valid ID's are: " + valids.toString());
                            //Wrong rtp world type
                        }
                    }/* else {
                        if (getPl().getSettings().debug)
                            getPl().getLogger().info("- World Type failed for '" + entry.getKey() + "' is it loaded?");
                    }*/
                }
            if (getPl().getSettings().debug)
                for (String world : world_type.keySet())
                    getPl().getLogger().info("- World Type for '" + world + "' set to '" + world_type.get(world) + "'");
        } catch (Exception e) {
            e.printStackTrace();
            //No World Types
        }
        teleport.load(); //Load teleporting stuff
    }

    public List<String> disabledWorlds() {
        return disabledWorlds;
    }

    @SuppressWarnings("unused")
    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public void start(Player p, CommandSender sendi, String worldName, List<String> biomes, boolean delay) {
        // Check overrides
        if (worldName == null)
            worldName = p.getWorld().getName();
        if (overriden.containsKey(worldName))
            worldName = overriden.get(worldName);
        // Not forced and has 'betterrtp.world.<world>'
        if (sendi == p && !getPl().getPerms().getAWorld(sendi, worldName)) {
            getPl().getCmd().cooldowns.remove(p.getUniqueId());
            getPl().getText().getNoPermissionWorld(p, worldName);
            return;
        }
        // Check disabled worlds
        if (disabledWorlds.contains(worldName)) {
            getPl().getText().getDisabledWorld(sendi, worldName);
            getPl().getCmd().cooldowns.remove(p.getUniqueId());
            return;
        }
        // Check if nulled or world doesnt exist
        if (Bukkit.getWorld(worldName) == null) {
            getPl().getText().getNotExist(sendi, worldName);
            getPl().getCmd().cooldowns.remove(p.getUniqueId());
            return;
        }
        PlayerWorld pWorld = new PlayerWorld(p, Bukkit.getWorld(worldName));
        // Set all methods
        if (customWorlds.containsKey(worldName)) {
            RTPWorld cWorld = customWorlds.get(pWorld.getWorld().getName());
            pWorld.setup(cWorld, cWorld.getPrice(), biomes);
        } else
            pWorld.setup(defaultWorld, defaultWorld.getPrice(), biomes);
        //World type
        RTP_WORLD_TYPE world_type = RTP_WORLD_TYPE.NORMAL; //World rtp type
        if (this.world_type.containsKey(worldName))
            world_type = this.world_type.get(worldName);
        pWorld.setWorldtype(world_type);
        // Check world price
        if (!getPl().getEco().charge(p, pWorld.getPrice())) {
            getPl().getText().getFailedPrice(p, pWorld.getPrice());
            getPl().getCmd().cooldowns.remove(p.getUniqueId());
            return;
        }
        // Delaying? Else, just go
        getPl().getCmd().rtping.put(p.getUniqueId(), true); //Cache player so they cant run '/rtp' again while rtp'ing
        if (getPl().getSettings().delayEnabled && delay) {
            new RTPDelay(sendi, pWorld, delayTime, cancelOnMove, cancelOnDamage);
        } else
            findSafeLocation(sendi, pWorld);
    }

//    void findSafeLocation(CommandSender sendi, PlayerWorld pWorld) {
//        new BukkitRunnable(){
//            @Override
//            public void run() {
//                f
//                if (loc != null)
//                    teleport.sendPlayer(sendi, pWorld.getPlayer(), loc, pWorld.getPrice(), pWorld.getAttempts());
//                else
//                    metMax(sendi, pWorld.getPlayer(), pWorld.getPrice());
//            }
//        }.runTaskAsynchronously(getPl());
//    }

    void findSafeLocation(CommandSender sendi, PlayerWorld pWorld) {
        if (pWorld.getAttempts() >= maxAttempts) //Cancel out, too many tried
            metMax(sendi, pWorld.getPlayer(), pWorld.getPrice());
        else { //Try again to find a safe location
            Location loc = pWorld.generateRandomXZ(defaultWorld, rn.nextInt(4)); //randomLoc(pWorld);
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
                    teleport.sendPlayer(sendi, pWorld.getPlayer(), tpLoc, pWorld.getPrice(), pWorld.getAttempts());
                else
                    findSafeLocation(sendi, pWorld);
            });
        }
    }

    // Compressed code for MaxAttempts being met
    private void metMax(CommandSender sendi, Player p, int price) {
        if (p == sendi)
            getPl().getText().getFailedNotSafe(sendi, maxAttempts);
        else
            getPl().getText().getOtherNotSafe(sendi, maxAttempts, p.getDisplayName());
        getPl().getCmd().cooldowns.remove(p.getUniqueId());
        getPl().getEco().unCharge(p, price);
        getPl().getCmd().rtping.put(p.getUniqueId(), false);
    }

    //Get a random location depending the world type
//    private Location randomLoc(PlayerWorld pWorld) {
//        int borderRad = pWorld.getMaxRad();
//        int minVal = pWorld.getMinRad();
//        int CenterX = pWorld.getCenterX();
//        int CenterZ = pWorld.getCenterZ();
//        int quadrant = rn.nextInt(4);
//        Player p = pWorld.getPlayer();
//        World world = pWorld.getWorld();
//        if (pWorld.getUseWorldborder()) {
//            WorldBorder border = world.getWorldBorder();
//            borderRad = (int) border.getSize() / 2;
//            CenterX = border.getCenter().getBlockX();
//            CenterZ = border.getCenter().getBlockZ();
//        }
//        float yaw = p.getLocation().getYaw(), pitch = p.getLocation().getPitch();
//        RTP_WORLD_TYPE world_type = pWorld.getWorldtype();
//        //for (int i = 0; i <= maxAttempts; i++) {
//            // Get the y-coords from up top, then check if it's SAFE!
//            Location loc = null;
//            if (borderRad <= minVal) {
//                minVal = defaultWorld.getMinRad();
//                if (borderRad <= minVal)
//                    minVal = 0;
//            }
//            switch (world_type) {
//                case NETHER:
//                    loc = nether(borderRad, minVal, CenterX, CenterZ, quadrant, world, pWorld, yaw, pitch); break;
//                default:
//                    loc = normal(borderRad, minVal, CenterX, CenterZ, quadrant, world, pWorld, yaw, pitch);
//            }
//            pWorld.addAttempt();
//            //if (loc != null && checkDepends(loc))
//                return loc;
//        //    quadrant = rn.nextInt(4);
//        //}
//        //return null;
//    }
//
//    //NORMAL
//    private Location normal(int borderRad, int minVal, int CenterX, int CenterZ, int quadrant, World world,
//                            PlayerWorld pWorld, Float yaw, Float pitch) {
//        int x, x2, z, z2;
//        Location loc;
//        // Will Check is CenterZ is negative or positive, then set 2 x's
//        // up for choosing up next
//        z = rn.nextInt(borderRad - minVal) + CenterZ + minVal;
//        z2 = -(rn.nextInt(borderRad - minVal) - CenterZ - minVal);
//        // Will Check is CenterZ is negative or positive, then set 2 z's
//        // up for choosing up next
//        x = rn.nextInt(borderRad - minVal) + CenterX + minVal;
//        x2 = -rn.nextInt(borderRad - minVal) + CenterX - minVal;
//        switch (quadrant) {
//            case 0: // Positive X and Z
//                loc = getLocAtNormal(x, z, world, yaw, pitch, pWorld); break;
//            case 1: // Negative X and Z
//                loc = getLocAtNormal(x2, z2, world, yaw, pitch, pWorld); break;
//            case 2: // Negative X and Positive Z
//                loc = getLocAtNormal(x2, z, world, yaw, pitch, pWorld); break;
//            default: // Positive X and Negative Z
//                loc = getLocAtNormal(x, z2, world, yaw, pitch, pWorld);
//        }
//        return loc;
//    }

    private Location getLocAtNormal(int x, int z, World world, Float yaw, Float pitch, PlayerWorld pWorld) {
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

//    //NETHER
//    private Location nether(int borderRad, int minVal, int CenterX, int CenterZ, int quadrant, World world,
//                            PlayerWorld pWorld, Float yaw, Float pitch) {
//        int x, x2, z, z2;
//        Location loc;
//        // Will Check is CenterZ is negative or positive, then set 2 x's
//        // up for choosing up next
//        z = rn.nextInt((borderRad) - minVal) + CenterZ + minVal;
//        z2 = -(rn.nextInt(borderRad - minVal) - CenterZ - minVal);
//        // Will Check is CenterZ is negative or positive, then set 2 z's
//        // up for choosing up next
//        x = rn.nextInt(borderRad - minVal) + CenterX + minVal;
//        x2 = -rn.nextInt(borderRad - minVal) + CenterX - minVal;
//        switch (quadrant) {
//            case 0: // Positive X and Z
//                loc = getLocAtNether(x, z, world, yaw, pitch, pWorld); break;
//            case 1: // Negative X and Z
//                loc = getLocAtNether(x2, z2, world, yaw, pitch, pWorld); break;
//            case 2: // Negative X and Positive Z
//                loc = getLocAtNether(x2, z, world, yaw, pitch, pWorld); break;
//            default: // Positive X and Negative Z
//                loc = getLocAtNether(x, z2, world, yaw, pitch, pWorld);
//        }
//        return loc;
//    }

    private Location getLocAtNether(int x, int z, World world, Float yaw, Float pitch, PlayerWorld pWorld) {
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

    @SuppressWarnings("all")
    private boolean checkDepends(Location loc) {
        try {
            if (getPl().getSettings().getsDepends().isWorldguard()) {
                WorldGuardPlugin plugin = WGBukkit.getPlugin();
                RegionContainer container = plugin.getRegionContainer();
                RegionManager regions = container.get(loc.getWorld());
                // Check to make sure that "regions" is not null
                return regions.getApplicableRegions(loc).size() == 0;
            }
            return !getPl().getSettings().getsDepends().isGriefprevention() || GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) == null;
        } catch (NoClassDefFoundError e) {
            return true;
        }
    }

    // Bad blocks, or bad biome
    private boolean badBlock(String block, int x, int z, World world, List<String> biomes) {
        for (String currentBlock : blockList) //Check Block
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