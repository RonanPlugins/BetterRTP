package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.player.commands.RTP_SETUP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPermissionGroup;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldPlayer implements RTPWorld {
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxRad, minRad, price, min_y, max_y;
    private List<String> Biomes;
    @Getter private final Player player;
    private final World world;
    private WORLD_TYPE world_type;
    private RTPPermissionGroup.RTPPermConfiguration config = null;
    private RTP_SHAPE shape;
    public RTP_SETUP_TYPE setup_type = RTP_SETUP_TYPE.DEFAULT;
    public String setup_name;
    //Economy
    public boolean eco_money_taken = false;
    @Getter private boolean setup = false;

    public WorldPlayer(Player p, World world) {
        this.player = p;
        this.world = world;
    }

    public void setup(String setup_name, RTPWorld world, List<String> biomes, boolean personal) {
        if (world instanceof WorldLocations) {
            setup_type = RTP_SETUP_TYPE.LOCATION;
        } else if (world instanceof WorldCustom)
            setup_type = RTP_SETUP_TYPE.CUSTOM_WORLD;
        this.setup_name = setup_name;
        setUseWorldborder(world.getUseWorldborder());
        setCenterX(world.getCenterX());
        setCenterZ(world.getCenterZ());
        setMaxRad(world.getMaxRadius());
        setMinRad(world.getMinRadius());
        setShape(world.getShape());
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
        if (getMaxRadius() <= getMinRadius()) {
            setMinRad(BetterRTP.getInstance().getRTP().defaultWorld.getMinRadius());
            if (getMaxRadius() <= getMinRadius())
                setMinRad(0);
        }
        //World border protection
        if (getUseWorldborder()) {
            WorldBorder border = getWorld().getWorldBorder();
            int _borderRad = (int) border.getSize() / 2;
            if (getMaxRadius() > _borderRad)
                setMaxRad(_borderRad);
            setCenterX(border.getCenter().getBlockX());
            setCenterZ(border.getCenter().getBlockZ());
        }
        //MinY
        setMinY(world.getMinY());
        setMaxY(world.getMaxY());
        //min_y = world.getWorld().getBlockAt(0, -1, 0).getType() != Material.AIR ?
        setup = true;
    }

    private void setupGroup(RTPPermissionGroup permConfig) {
        RTPPermissionGroup.RTPPermConfiguration config = permConfig.getGroup(player);
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

    public boolean checkIsValid(Location loc) { //Will check if a previously given location is valid
        if (loc.getWorld() != getWorld())
            return false;
        int _xLMax = getCenterX() - getMaxRadius(); //I|-||
        int _xLMin = getCenterX() - getMinRadius(); //|I-||
        int _xRMax = getCenterX() + getMaxRadius(); //||-|I
        int _xRMin = getCenterX() + getMinRadius(); //||-I|
        int _xLoc = loc.getBlockX();
        if (_xLoc < _xLMax || (_xLoc > _xLMin && _xLoc < _xRMin) || _xLoc > _xRMax)
            return false;
        int _zLMax = getCenterZ() - getMaxRadius(); //I|-||
        int _zLMin = getCenterZ() - getMinRadius(); //|I-||
        int _zRMax = getCenterZ() + getMaxRadius(); //||-|I
        int _zRMin = getCenterZ() + getMinRadius(); //||-I|
        int _zLoc = loc.getBlockX();
        return _zLoc >= _zLMax && (_zLoc <= _zLMin || _zLoc >= _zRMin) && _zLoc <= _zRMax;
    }

    public Location generateLocation() {
        Location loc;
        switch (shape) {
            case CIRCLE:
                loc = generateRound(getMaxRadius(), getMinRadius()); break;
            default:
                loc = generateSquare(getMaxRadius(), getMinRadius()); break;
        }
        return loc;
    }

    private Location generateSquare(int maxRad, int min) {
        //Generate a random X and Z based off the quadrant selected
        int max = maxRad - min;
        int x, z;
        int quadrant = new Random().nextInt(4);
        switch (quadrant) {
            case 0: // Positive X and Z
                x = new Random().nextInt(max) + min;
                z = new Random().nextInt(max) + min; break;
            case 1: // Negative X and Z
                x = -new Random().nextInt(max) - min;
                z = -(new Random().nextInt(max) + min); break;
            case 2: // Negative X and Positive Z
                x = -new Random().nextInt(max) - min;
                z = new Random().nextInt(max) + min;  break;
            default: // Positive X and Negative Z
                x = new Random().nextInt(max) + min;
                z = -(new Random().nextInt(max) + min); break;
        }
        x += getCenterX();
        z += getCenterZ();
        //System.out.println(quadrant);
        return new Location(getWorld(), x, 0, z);
    }

    private Location generateRound(int maxRad, int min) {
        //Generate a random X and Z based off location on a spiral curve
        int max = maxRad - min;
        int x, z;

        double area = Math.PI * (max - min) * (max + min); //of all the area in this donut
        double subArea = area * new Random().nextDouble(); //pick a random subset of that area

        double r = Math.sqrt(subArea/Math.PI + min*min); //convert area to radius
        double theta = (r - (int) r) * 2 * Math.PI; //use the remainder as an angle

        // polar to cartesian
        x = (int) (r * Math.cos(theta));
        z = (int) (r * Math.sin(theta));
        x += getCenterX();
        z += getCenterZ();
        return new Location(getWorld(), x, 0, z);
    }

    @NotNull
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
    public int getMaxRadius() {
        return maxRad;
    }

    @Override
    public int getMinRadius() {
        return minRad;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public List<String> getBiomes() {
        return Biomes;
    }

    @Override
    public RTP_SHAPE getShape() {
        return shape;
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
        maxRad = max;
    }

    private void setMinRad(int min) {
        minRad = min;
    }

    private void setPrice(int price) {
        this.price = price;
    }

    //
    private void setBiomes(List<String> biomes) {
        this.Biomes = biomes;
    }

    //Custom World type
    public void setWorldtype(WORLD_TYPE type) {
        this.world_type = type;
    }

    public void setShape(RTP_SHAPE shape) {
        this.shape = shape;
    }

    public void setMinY(int value) {
        this.min_y = value;
    }

    public void setMaxY(int value) {
        this.max_y = value;
    }

    public RTPPermissionGroup.RTPPermConfiguration getConfig() {
        return this.config;
    }

    public WORLD_TYPE getWorldtype() {
        return this.world_type;
    }

    public int getMinY() {
        return min_y;
    }

    @Override
    public int getMaxY() {
        return max_y;
    }
}
