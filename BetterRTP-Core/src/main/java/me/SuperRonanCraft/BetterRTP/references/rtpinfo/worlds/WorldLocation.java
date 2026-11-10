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

public class WorldLocation implements RTPWorld, RTPWorld_Defaulted {
    private boolean useWorldborder, RTPOnDeath;
    private int centerX, centerZ, maxRad, minRad, price, miny, maxy;
    private long cooldown;
    private List<String> biomes;
    private World world;
    private RTP_SHAPE shape;
    private final String name;

    public WorldLocation(String location_name) {
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
                Map<?, ?> section = ((Map<?, ?>) m.get(key));
                if (section == null)
                    continue;
                if (section.get("World") != null) {
                    if (section.get("World").getClass() == String.class) {
                        world = Bukkit.getWorld(section.get("World").toString());
                        if (world != null)
                            BetterRTP.debug("- - World: " + world.getName());
                        else
                            BetterRTP.getInstance().getLogger().warning("Location `" + location_name + "` is declared, but the world " + section.get("World").toString() + " doesn't exist!");
                    }
                }
                if (world == null) {
                    BetterRTP.getInstance().getLogger().warning("Location `" + location_name + "` does NOT have a `World` or world doesnt exist!");
                    return;
                }
                if (section.get("UseWorldBorder") != null) {
                    if (section.get("UseWorldBorder").getClass() == Boolean.class) {
                        useWorldborder = Boolean.parseBoolean(section.get("UseWorldBorder").toString());
                        BetterRTP.debug("- - UseWorldBorder: " + useWorldborder);
                    }
                }
                if (section.get("CenterX") != null) {
                    if (section.get("CenterX").getClass() == Integer.class) {
                        centerX = Integer.parseInt((section.get("CenterX")).toString());
                        BetterRTP.debug("- - World: " + centerX);
                    }
                }
                if (section.get("CenterZ") != null) {
                    if (section.get("CenterZ").getClass() == Integer.class) {
                        centerZ = Integer.parseInt((section.get("CenterZ")).toString());
                        BetterRTP.debug("- - CenterZ: " + centerZ);
                    }
                }
                if (section.get("MaxRadius") != null) {
                    if (section.get("MaxRadius").getClass() == Integer.class)
                        maxRad = Integer.parseInt((section.get("MaxRadius")).toString());
                    if (maxRad <= 0) {
                        Message_RTP.sms(Bukkit.getConsoleSender(),
                                "WARNING! Location '" + location_name + "' Maximum radius of '" + maxRad + "' is not allowed! Set to default value!");
                        maxRad = BetterRTP.getInstance().getRTP().getRTPdefaultWorld().getMaxRadius();
                    }
                    BetterRTP.debug("- - MaxRadius: " + maxRad);
                }
                if (section.get("MinRadius") != null) {
                    if (section.get("MinRadius").getClass() == Integer.class)
                        minRad = Integer.parseInt((section.get("MinRadius")).toString());
                    if (minRad < 0 || minRad >= maxRad) {
                        Message_RTP.sms(Bukkit.getConsoleSender(),
                                "WARNING! Location '" + location_name + "' Minimum radius of '" + minRad + "' is not allowed! Set to default value!");
                        minRad = BetterRTP.getInstance().getRTP().getRTPdefaultWorld().getMinRadius();
                        if (minRad >= maxRad) {
                            maxRad = BetterRTP.getInstance().getRTP().getRTPdefaultWorld().getMaxRadius();
                            BetterRTP.debug("- ! MaxRadius: " + maxRad);
                        }
                    }
                    BetterRTP.debug("- - MinRad: " + minRad);
                }
                if (section.get("Biomes") != null) {
                    if (section.get("Biomes").getClass() == ArrayList.class) {
                        this.biomes = new ArrayList<String>((ArrayList) section.get("Biomes"));
                        BetterRTP.debug("- - Biomes: " + this.biomes);
                    }
                }
                if (BetterRTP.getInstance().getFiles().getType(FileOther.FILETYPE.ECO).getBoolean("Economy.Enabled"))
                    if (section.get("Price") != null) {
                        if (section.get("Price").getClass() == Integer.class) {
                            this.price = Integer.parseInt(section.get("Price").toString());
                            BetterRTP.debug("- - Price: " + price);
                        }
                        //else
                           // price = worldDefault.getPrice(world);
                    } //else
                        //price = worldDefault.getPrice(world);
                if (section.get("Shape") != null) {
                    if (section.get("Shape").getClass() == String.class) {
                        try {
                            this.shape = RTP_SHAPE.valueOf(section.get("Shape").toString().toUpperCase());
                            BetterRTP.debug("- - Shape: " + shape.name());
                        } catch (Exception e) {
                            //Invalid shape
                        }
                    }
                }
                if (section.get("UseWorldBorder") != null) {
                    if (section.get("UseWorldBorder").getClass() == Boolean.class) {
                        try {
                            this.useWorldborder = Boolean.parseBoolean(section.get("UseWorldBorder").toString());
                            BetterRTP.debug("- - UseWorldBorder: " + useWorldborder);
                        } catch (Exception e) {
                            //No UseWorldBorder
                        }
                    }
                }
                if (section.get("MinY") != null)
                    if (section.get("MinY").getClass() == Integer.class) {
                        this.miny = Integer.parseInt(section.get("MinY").toString());
                        BetterRTP.debug("- - MinY: " + miny);
                    }
                if (section.get("MaxY") != null)
                    if (section.get("MaxY").getClass() == Integer.class) {
                        this.maxy = Integer.parseInt(section.get("MaxY").toString());
                        BetterRTP.debug("- - MaxY: " + maxy);
                    }
                if (section.get("Cooldown") != null)
                    if (section.get("Cooldown").getClass() == Integer.class || section.get("Cooldown").getClass() == Long.class) {
                        this.cooldown = Long.parseLong(section.get("Cooldown").toString());
                        BetterRTP.debug("- - Cooldown: " + cooldown);
                    }
                if (section.get("RTPOnDeath") != null) {
                    if (section.get("RTPOnDeath").getClass() == Boolean.class) {
                        RTPOnDeath = Boolean.parseBoolean(section.get("RTPOnDeath").toString());
                        BetterRTP.debug("- - RTPOnDeath: " + RTPOnDeath);
                    }
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

    @Override public boolean getRTPOnDeath() {
        return RTPOnDeath;
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

    @Override public void setRTPOnDeath(boolean bool) {
        this.RTPOnDeath = bool;
    }
}
