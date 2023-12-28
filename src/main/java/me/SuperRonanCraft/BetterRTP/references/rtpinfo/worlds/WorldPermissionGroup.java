package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import lombok.Getter;
import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("rawtypes")
public class WorldPermissionGroup implements RTPWorld, RTPWorld_Defaulted {
    private boolean useWorldborder, RTPOnDeath;
    private int centerX, centerZ, maxRad, minRad, price, miny, maxy;
    private List<String> biomes;
    public World world;
    private RTP_SHAPE shape;
    @Getter private int priority;
    @Getter private final String groupName;
    private long cooldown;

    public WorldPermissionGroup(String group, World world, Map.Entry fields) {
        this.groupName = group;
        this.world = world;
        setupDefaults();

        this.priority = 0;
        //Find Location and cache its values
        for (Object hash2 : ((HashMap) fields.getValue()).entrySet()) {
            Map.Entry hash3 = (Map.Entry) hash2;
            String field = hash3.getKey().toString();
            if (field.equalsIgnoreCase("Priority")) {
                if (hash3.getValue().getClass() == Integer.class) {
                    priority = Integer.parseInt((hash3.getValue()).toString());
                    BetterRTP.debug("- - Priority: " + priority);
                }
            }
            if (field.equalsIgnoreCase("UseWorldBorder")) {
                if (hash3.getValue().getClass() == Boolean.class) {
                    useWorldborder = Boolean.parseBoolean(hash3.getValue().toString());
                    BetterRTP.debug("- - UseWorldBorder: " + useWorldborder);
                }
            }
            if (field.equalsIgnoreCase("CenterX")) {
                if (hash3.getValue().getClass() == Integer.class) {
                    centerX = Integer.parseInt((hash3.getValue()).toString());
                    BetterRTP.debug("- - CenterX: " + centerX);
                }
            }
            if (field.equalsIgnoreCase("CenterZ")) {
                if (hash3.getValue().getClass() == Integer.class) {
                    centerZ = Integer.parseInt((hash3.getValue()).toString());
                    BetterRTP.debug("- - CenterZ: " + centerZ);
                }
            }
            if (field.equalsIgnoreCase("MaxRadius")) {
                if (hash3.getValue().getClass() == Integer.class) {
                    maxRad = Integer.parseInt((hash3.getValue()).toString());
                    BetterRTP.debug("- - MaxRadius: " + maxRad);
                }
                if (maxRad <= 0) {
                    Message_RTP.sms(Bukkit.getConsoleSender(),
                            "WARNING! Group '" + group + "' Maximum radius of '" + maxRad + "' is not allowed! Set to default value!");
                    maxRad = BetterRTP.getInstance().getRTP().getRTPdefaultWorld().getMaxRadius();
                }
            }
            if (field.equalsIgnoreCase("MinRadius")) {
                if (hash3.getValue().getClass() == Integer.class) {
                    minRad = Integer.parseInt((hash3.getValue()).toString());
                    BetterRTP.debug("- - MinRadius: " + minRad);
                }
                if (minRad < 0 || minRad >= maxRad) {
                    Message_RTP.sms(Bukkit.getConsoleSender(),
                            "WARNING! Group '" + group + "' Minimum radius of '" + minRad + "' is not allowed! Set to default value!");
                    minRad = BetterRTP.getInstance().getRTP().getRTPdefaultWorld().getMinRadius();
                    if (minRad >= maxRad)
                        maxRad = BetterRTP.getInstance().getRTP().getRTPdefaultWorld().getMaxRadius();
                }
            }
            if (field.equalsIgnoreCase("Biomes")) {
                if (hash3.getValue().getClass() == ArrayList.class) {
                    this.biomes = new ArrayList<String>((ArrayList) hash3.getValue());
                    BetterRTP.debug("- - Biomes: " + biomes);
                }
            }
            if (FileOther.FILETYPE.ECO.getBoolean("Economy.Enabled"))
                if (field.equalsIgnoreCase("Price")) {
                    if (hash3.getValue().getClass() == Integer.class) {
                        this.price = Integer.parseInt(hash3.getValue().toString());
                        BetterRTP.debug("- - Price: " + price);
                    }
                    //else
                    // price = worldDefault.getPrice(world);
                } //else
            //price = worldDefault.getPrice(world);
            if (field.equalsIgnoreCase("Shape")) {
                if (hash3.getValue().getClass() == String.class) {
                    try {
                        this.shape = RTP_SHAPE.valueOf(hash3.getValue().toString().toUpperCase());
                        BetterRTP.debug("- - Shape: " + shape.name());
                    } catch (Exception e) {
                        BetterRTP.debug("- - Shape: (INVALID) " + hash3.getValue().toString());
                        //Invalid shape
                    }
                }
            }
            if (field.equalsIgnoreCase("MinY"))
                if (hash3.getValue().getClass() == Integer.class) {
                    this.miny = Integer.parseInt(hash3.getValue().toString());
                    BetterRTP.debug("- - MinY: " + miny);
                }
            if (field.equalsIgnoreCase("MaxY"))
                if (hash3.getValue().getClass() == Integer.class) {
                    this.maxy = Integer.parseInt(hash3.getValue().toString());
                    BetterRTP.debug("- - MaxY: " + maxy);
                }
            if (field.equalsIgnoreCase("Cooldown"))
                if (hash3.getValue().getClass() == Integer.class || hash3.getValue().getClass() == Long.class) {
                    this.cooldown = Long.parseLong(hash3.getValue().toString());
                    BetterRTP.debug("- - Cooldown: " + cooldown);
                }
            if (field.equalsIgnoreCase("RTPOnDeath")) {
                if (hash3.getValue().getClass() == Boolean.class) {
                    RTPOnDeath = Boolean.parseBoolean(hash3.getValue().toString());
                    BetterRTP.debug("- - RTPOnDeath: " + RTPOnDeath);
                }
            }
        }
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

    @NotNull @Override
    public @NonNull World getWorld() {
        return world;
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

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override public boolean getRTPOnDeath() {
        return RTPOnDeath;
    }

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
