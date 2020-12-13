package me.SuperRonanCraft.BetterRTP.references.worlds;

import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPermissionGroup;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldPlayer implements RTPWorld {
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price, attempts;
    private List<String> Biomes;
    private final CommandSender p;
    private final World world;
    private WORLD_TYPE world_type;
    private RTPPermissionGroup.RTPPermConfiguration config = null;
    //Economy
    public boolean eco_money_taken = false;

    public WorldPlayer(CommandSender p, World world) {
        this.p = p;
        this.world = world;
    }

    public void setup(RTPWorld world, List<String> biomes, boolean personal) {
        setUseWorldborder(world.getUseWorldborder());
        setCenterX(world.getCenterX());
        setCenterZ(world.getCenterZ());
        setMaxRad(world.getMaxRad());
        setMinRad(world.getMinRad());
        if (world instanceof WorldDefault)
            setPrice(((WorldDefault) world).getPrice(getWorld().getName()));
        else
            setPrice(world.getPrice());
        List<String> list = new ArrayList<>(world.getBiomes());
        if (biomes != null) {
            list.clear();
            list.addAll(biomes);
        }
        setBiomes(list);
        if (personal)
            setupGroup(BetterRTP.getInstance().getRTP().permConfig);
        //Make sure our borders will not cause an invalid integer
        if (getMaxRad() <= getMinRad()) {
            setMinRad(BetterRTP.getInstance().getRTP().defaultWorld.getMinRad());
            if (getMaxRad() <= getMinRad())
                setMinRad(0);
        }
        //World border protection
        if (getUseWorldborder()) {
            WorldBorder border = getWorld().getWorldBorder();
            int _borderRad = (int) border.getSize() / 2;
            if (getMaxRad() > _borderRad)
                setMaxRad(_borderRad);
            setCenterX(border.getCenter().getBlockX());
            setCenterZ(border.getCenter().getBlockZ());
        }
    }

    private void setupGroup(RTPPermissionGroup permConfig) {
        RTPPermissionGroup.RTPPermConfiguration config = permConfig.getGroup(p);
        if (config != null) {
            for (RTPPermissionGroup.RTPPermConfigurationWorld world : config.worlds) {
                if (getWorld().getName().equals(world.name)) {
                    if (world.maxRad != -1)
                        setMaxRad(world.maxRad);
                    if (world.minRad != -1)
                        setMinRad(world.minRad);
                    if (world.price != -1)
                        setPrice(world.price);
                    if (world.centerx != -1)
                        setCenterX(world.centerx);
                    if (world.centerz != -1)
                        setCenterZ(world.centerz);
                    if (world.useworldborder != null)
                        setUseWorldborder((Boolean) world.useworldborder);
                    this.config = config;
                    break;
                }
            }
        }
    }

    public Player getPlayer() {
        return (Player) p;
    }

    public Location generateRandomXZ(WorldDefault defaultWorld) {
        int borderRad = getMaxRad();
        int minVal = getMinRad();

        //Generate a random X and Z based off the quadrant selected
        int max = borderRad - minVal;
        int x, z;
        int quadrant = new Random().nextInt(4);
        switch (quadrant) {
            case 0: // Positive X and Z
                x = new Random().nextInt(max) + minVal;
                z = new Random().nextInt(max) + minVal; break;
            case 1: // Negative X and Z
                x = -new Random().nextInt(max) - minVal;
                z = -(new Random().nextInt(max) + minVal); break;
            case 2: // Negative X and Positive Z
                x = -new Random().nextInt(max) - minVal;
                z = new Random().nextInt(max) + minVal;  break;
            default: // Positive X and Negative Z
                x = new Random().nextInt(max) + minVal;
                z = -(new Random().nextInt(max) + minVal); break;
        }
        x += getCenterX();
        z += getCenterZ();
        addAttempt();
        //System.out.println(quadrant);
        return new Location(getWorld(), x, 0, z);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean getUseWorldborder() {
        return useWorldborder;
    }

    @Override
    public int getCenterX() {
        return CenterX;
    }

    @Override
    public int getCenterZ() {
        return CenterZ;
    }

    @Override
    public int getMaxRad() {
        return maxBorderRad;
    }

    @Override
    public int getMinRad() {
        return minBorderRad;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public int getAttempts() {return attempts; }

    @Override
    public List<String> getBiomes() {
        return Biomes;
    }

    private void setUseWorldborder(boolean bool) {
        useWorldborder = bool;
    }

    private void setCenterX(int x) {
        CenterX = x;
    }

    private void setCenterZ(int z) {
        CenterZ = z;
    }

    //Modifiable
    private void setMaxRad(int max) {
        maxBorderRad = max;
    }

    private void setMinRad(int min) {
        minBorderRad = min;
    }

    private void setPrice(int price) {
        this.price = price;
    }

    public void addAttempt() {
        this.attempts++;
    }

    //
    private void setBiomes(List<String> biomes) {
        this.Biomes = biomes;
    }

    //Custom World type
    public void setWorldtype(WORLD_TYPE type) {
        this.world_type = type;
    }

    public RTPPermissionGroup.RTPPermConfiguration getConfig() {
        return this.config;
    }

    public WORLD_TYPE getWorldtype() {
        return this.world_type;
    }
}
