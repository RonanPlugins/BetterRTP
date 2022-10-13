package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldLocations implements RTPWorld, RTPWorld_Defaulted {
    private boolean useWorldborder;
    private int centerX, centerZ, maxRad, minRad, price, miny, maxy;
    private long cooldown;
    private List<String> biomes;
    private World world;
    private RTP_SHAPE shape;
    private final String name;

    public WorldLocations(String location_name) {
        FileOther.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileOther.FILETYPE.LOCATIONS);
        List<Map<?, ?>> map = config.getMapList("Locations");
        //WorldDefault worldDefault = BetterRTP.getInstance().getRTP().defaultWorld;

        setupDefaults();
        this.name = location_name;

        BetterRTP.debug("- Loading Location " + location_name + ":");
        //Find Location and cache its values
        for (Map<?, ?> m : map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                String key = entry.getKey().toString();
                if (!key.equals(location_name))
                    continue;
                Map<?, ?> test = ((Map<?, ?>) m.get(key));
                if (test == null)
                    continue;
                if (test.get("World") != null) {
                    if (test.get("World").getClass() == String.class) {
                        world = Bukkit.getWorld(test.get("World").toString());
                        BetterRTP.debug("- - World: " + world.getName());
                    }
                }
                if (world == null) {
                    BetterRTP.getInstance().getLogger().warning("Location `" + location_name + "` does NOT have a `World` or world doesnt exist!");
                    return;
                }
                if (test.get("UseWorldBorder") != null) {
                    if (test.get("UseWorldBorder").getClass() == Boolean.class) {
                        useWorldborder = Boolean.parseBoolean(test.get("UseWorldBorder").toString());
                        BetterRTP.debug("- - UseWorldBorder: " + useWorldborder);
                    }
                }
                if (test.get("CenterX") != null) {
                    if (test.get("CenterX").getClass() == Integer.class) {
                        centerX = Integer.parseInt((test.get("CenterX")).toString());
                        BetterRTP.debug("- - World: " + centerX);
                    }
                }
                if (test.get("CenterZ") != null) {
                    if (test.get("CenterZ").getClass() == Integer.class) {
                        centerZ = Integer.parseInt((test.get("CenterZ")).toString());
                        BetterRTP.debug("- - CenterZ: " + centerZ);
                    }
                }
                if (test.get("MaxRadius") != null) {
                    if (test.get("MaxRadius").getClass() == Integer.class)
                        maxRad = Integer.parseInt((test.get("MaxRadius")).toString());
                    if (maxRad <= 0) {
                        Message_RTP.sms(Bukkit.getConsoleSender(),
                                "WARNING! Location '" + location_name + "' Maximum radius of '" + maxRad + "' is not allowed! Set to default value!");
                        maxRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMaxRadius();
                    }
                    BetterRTP.debug("- - MaxRadius: " + maxRad);
                }
                if (test.get("MinRadius") != null) {
                    if (test.get("MinRadius").getClass() == Integer.class)
                        minRad = Integer.parseInt((test.get("MinRadius")).toString());
                    if (minRad < 0 || minRad >= maxRad) {
                        Message_RTP.sms(Bukkit.getConsoleSender(),
                                "WARNING! Location '" + location_name + "' Minimum radius of '" + minRad + "' is not allowed! Set to default value!");
                        minRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMinRadius();
                        if (minRad >= maxRad) {
                            maxRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMaxRadius();
                            BetterRTP.debug("- ! MaxRadius: " + maxRad);
                        }
                    }
                    BetterRTP.debug("- - MinRad: " + minRad);
                }
                if (test.get("Biomes") != null) {
                    if (test.get("Biomes").getClass() == ArrayList.class) {
                        this.biomes = new ArrayList<String>((ArrayList) test.get("Biomes"));
                        BetterRTP.debug("- - Biomes: " + this.biomes);
                    }
                }
                if (BetterRTP.getInstance().getFiles().getType(FileOther.FILETYPE.ECO).getBoolean("Economy.Enabled"))
                    if (test.get("Price") != null) {
                        if (test.get("Price").getClass() == Integer.class) {
                            this.price = Integer.parseInt(test.get("Price").toString());
                            BetterRTP.debug("- - Price: " + price);
                        }
                        //else
                           // price = worldDefault.getPrice(world);
                    } //else
                        //price = worldDefault.getPrice(world);
                if (test.get("Shape") != null) {
                    if (test.get("Shape").getClass() == String.class) {
                        try {
                            this.shape = RTP_SHAPE.valueOf(test.get("Shape").toString().toUpperCase());
                            BetterRTP.debug("- - Shape: " + shape.name());
                        } catch (Exception e) {
                            //Invalid shape
                        }
                    }
                }
                if (test.get("UseWorldBorder") != null) {
                    if (test.get("UseWorldBorder").getClass() == Boolean.class) {
                        try {
                            this.useWorldborder = Boolean.parseBoolean(test.get("UseWorldBorder").toString());
                            BetterRTP.debug("- - UseWorldBorder: " + useWorldborder);
                        } catch (Exception e) {
                            //No UseWorldBorder
                        }
                    }
                }
                if (test.get("MinY") != null)
                    if (test.get("MinY").getClass() == Integer.class) {
                        this.miny = Integer.parseInt(test.get("MinY").toString());
                        BetterRTP.debug("- - MinY: " + miny);
                    }
                if (test.get("MaxY") != null)
                    if (test.get("MaxY").getClass() == Integer.class) {
                        this.maxy = Integer.parseInt(test.get("MaxY").toString());
                        BetterRTP.debug("- - MaxY: " + maxy);
                    }
                if (test.get("Cooldown") != null)
                    if (test.get("Cooldown").getClass() == Integer.class || test.get("Cooldown").getClass() == Long.class) {
                        this.cooldown = Long.parseLong(test.get("Cooldown").toString());
                        BetterRTP.debug("- - Cooldown: " + cooldown);
                    }
            }
        }
    }

    public boolean isValid() {
        return world != null;
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
        return centerX;
    }

    @Override
    public int getCenterZ() {
        return centerZ;
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
        return biomes;
    }

    @Override
    public RTP_SHAPE getShape() {
        return shape;
    }

    @Override
    public int getMinY() {
        return miny;
    }

    @Override
    public int getMaxY() {
        return maxy;
    }

    @Nullable
    @Override
    public String getID() {
        return name;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }
    //Setters

    @Override
    public void setUseWorldBorder(boolean value) {
        this.useWorldborder = value;
    }

    @Override
    public void setCenterX(int value) {
        this.centerX = value;
    }

    @Override
    public void setCenterZ(int value) {
        this.centerZ = value;
    }

    @Override
    public void setMaxRadius(int value) {
        this.maxRad = value;
    }

    @Override
    public void setMinRadius(int value) {
        this.minRad = value;
    }

    @Override
    public void setPrice(int value) {
        this.price = value;
    }

    @Override
    public void setBiomes(List<String> value) {
        this.biomes = value;
    }

    @Override
    public void setWorld(World value) {
        this.world = value;
    }

    @Override
    public void setShape(RTP_SHAPE value) {
        this.shape = value;
    }

    @Override
    public void setMinY(int value) {
        this.miny = value;
    }

    @Override
    public void setMaxY(int value) {
        this.maxy = value;
    }

    @Override
    public void setCooldown(long value) {
        this.cooldown = value;
    }
}
