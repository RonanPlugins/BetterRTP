package me.SuperRonanCraft.BetterRTP.references.worlds;

import me.SuperRonanCraft.BetterRTP.player.commands.RTP_SETUP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPermissionGroup;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
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
    private final Player p;
    private final World world;
    private WORLD_TYPE world_type;
    private RTPPermissionGroup.RTPPermConfiguration config = null;
    private RTP_SHAPE shape;
    public RTP_SETUP_TYPE setup_type = RTP_SETUP_TYPE.DEFAULT;
    public String setup_name;
    //Economy
    public boolean eco_money_taken = false, setup = false;

    public WorldPlayer(Player p, World world) {
        this.p = p;
        this.world = world;
    }

    public boolean isSetup() {
        return setup;
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
        setMaxRad(world.getMaxRad());
        setMinRad(world.getMinRad());
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
        setup = true;
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
        return p;
    }

    public boolean checkIsValid(Location loc) { //Will check if a previously given location is valid
        if (loc.getWorld() != getWorld())
            return false;
        int _xLMax = getCenterX() - getMaxRad(); //I|-||
        int _xLMin = getCenterX() - getMinRad(); //|I-||
        int _xRMax = getCenterX() + getMaxRad(); //||-|I
        int _xRMin = getCenterX() + getMinRad(); //||-I|
        int _xLoc = loc.getBlockX();
        if (_xLoc < _xLMax || (_xLoc > _xLMin && _xLoc < _xRMin) || _xLoc > _xRMax)
            return false;
        int _zLMax = getCenterZ() - getMaxRad(); //I|-||
        int _zLMin = getCenterZ() - getMinRad(); //|I-||
        int _zRMax = getCenterZ() + getMaxRad(); //||-|I
        int _zRMin = getCenterZ() + getMinRad(); //||-I|
        int _zLoc = loc.getBlockX();
        return _zLoc >= _zLMax && (_zLoc <= _zLMin || _zLoc >= _zRMin) && _zLoc <= _zRMax;
    }

    public Location generateLocation() {
        Location loc = null;
        switch (shape) {
            case CIRCLE: //DISABLED UNTIL NEXT PATCH
                loc = generateRound(getMaxRad(), getMinRad()); break;
            default:
                loc = generateSquare(getMaxRad(), getMinRad()); break;
        }

        addAttempt(); //Add an attempt to keep track of the times we've attempted to generate a good location
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
        //Generate a random X and Z based off the quadrant selected
        int max = maxRad - min;
        int x, z;
        double r = max * Math.sqrt(new Random().nextDouble()) + min;
        double theta = (new Random().nextDouble()) * 2 * Math.PI;
        x = (int) (r * Math.cos(theta));
        z = (int) (r * Math.sin(theta));
        x += getCenterX();
        z += getCenterZ();
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

    public void setShape(RTP_SHAPE shape) {
        this.shape = shape;
    }

    public RTPPermissionGroup.RTPPermConfiguration getConfig() {
        return this.config;
    }

    public WORLD_TYPE getWorldtype() {
        return this.world_type;
    }
}
