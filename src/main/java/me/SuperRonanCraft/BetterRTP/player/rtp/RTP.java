package me.SuperRonanCraft.BetterRTP.player.rtp;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RTP {

    private final RTPTeleport teleport = new RTPTeleport();
    private final RTPPluginValidation softDepends = new RTPPluginValidation();
    //Cache
    public HashMap<String, RTPWorld> customWorlds = new HashMap<>();
    public HashMap<String, String> overriden = new HashMap<>();
    public Default defaultWorld = new Default();
    private List<String> disabledWorlds, blockList;
    private int maxAttempts, delayTime;
    private boolean cancelOnMove, cancelOnDamage;
    public HashMap<String, RTP_WORLD_TYPE> world_type = new HashMap<>();

    public RTPTeleport getTeleport() {
        return teleport;
    }

    public void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
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

        loadWorldSettings();

        teleport.load(); //Load teleporting stuff
    }

    public void loadWorldSettings() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        defaultWorld.setup();
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
    }

    public List<String> disabledWorlds() {
        return disabledWorlds;
    }

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

    void findSafeLocation(CommandSender sendi, PlayerWorld pWorld) {
        if (pWorld.getAttempts() >= maxAttempts) //Cancel out, too many tried
            metMax(sendi, pWorld.getPlayer(), pWorld.getPrice());
        else { //Try again to find a safe location
            Location loc = pWorld.generateRandomXZ(defaultWorld); //randomLoc(pWorld);
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

    private boolean checkDepends(Location loc) {
        return softDepends.checkLocation(loc);
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