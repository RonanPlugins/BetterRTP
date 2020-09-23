package me.SuperRonanCraft.BetterRTP.references.worlds;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldPlayer implements RTPWorld {
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price, attempts;
    private List<String> Biomes;
    private final Player p;
    private final World world;
    private WORLD_TYPE world_type;

    public WorldPlayer(Player p, World world) {
        this.p = p;
        this.world = world;
    }

    public void setup(RTPWorld world, int price, List<String> biomes) {
        setUseWorldborder(world.getUseWorldborder());
        setCenterX(world.getCenterX());
        setCenterZ(world.getCenterZ());
        setMaxRad(world.getMaxRad());
        setMinRad(world.getMinRad());
        setPrice(price);
        List<String> list = new ArrayList<>(world.getBiomes());
        if (biomes != null)
            list.addAll(biomes);
        setBiomes(list);
    }

    public Player getPlayer() {
        return p;
    }

    public Location generateRandomXZ(WorldDefault defaultWorld) {
        int borderRad = getMaxRad();
        int minVal = getMinRad();
        int CenterX = getCenterX();
        int CenterZ = getCenterZ();
        World world = getWorld();
        if (getUseWorldborder()) {
            WorldBorder border = world.getWorldBorder();
            borderRad = (int) border.getSize() / 2;
            CenterX = border.getCenter().getBlockX();
            CenterZ = border.getCenter().getBlockZ();
        }

        //Make sure our borders will not cause an invalid integer
        if (borderRad <= minVal) {
            minVal = defaultWorld.getMinRad();
            if (borderRad <= minVal)
                minVal = 0;
        }

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
        x += CenterX;
        z += CenterZ;
        addAttempt();
        //System.out.println(quadrant);
        return new Location(world, x, 0, z);
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

    private void setBiomes(List<String> biomes) {
        this.Biomes = biomes;
    }

    //Custom World type
    public void setWorldtype(WORLD_TYPE type) {
        this.world_type = type;
    }

    public WORLD_TYPE getWorldtype() {
        return this.world_type;
    }
}
