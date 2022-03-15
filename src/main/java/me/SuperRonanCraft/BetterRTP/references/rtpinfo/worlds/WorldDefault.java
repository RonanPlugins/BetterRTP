package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;


import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldDefault implements RTPWorld {
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price, miny, maxy;
    private List<String> Biomes;
    private final HashMap<String, Integer> prices = new HashMap<>();
    private RTP_SHAPE shape;

    public void setup() {
        //Setups
        String pre = "Default";
        FileBasics.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        //Booleans
        useWorldborder = config.getBoolean(pre + ".UseWorldBorder");
        //Integers
        CenterX = config.getInt(pre + ".CenterX");
        CenterZ = config.getInt(pre + ".CenterZ");
        maxBorderRad = config.getInt(pre + ".MaxRadius");
        try {
            shape = RTP_SHAPE.valueOf(config.getString(pre + ".Shape").toUpperCase());
        } catch (Exception e) {
            shape = RTP_SHAPE.SQUARE;
        }
        if (maxBorderRad <= 0) {
            BetterRTP.getInstance().getLogger().warning("WARNING! Default Maximum radius of '" + maxBorderRad + "' is not allowed! Value set to '1000'");
            maxBorderRad = 1000;
        }
        minBorderRad = config.getInt(pre + ".MinRadius");
        if (minBorderRad < 0 || minBorderRad >= maxBorderRad) {
            BetterRTP.getInstance().getLogger().warning("The Default MinRadius of '" + minBorderRad + "' is not allowed! Value set to '0'");
            minBorderRad = 0;
        }
        prices.clear();
        if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled")) {
            price = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getInt("Economy.Price");
            if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("CustomWorlds.Enabled")) {
                List<Map<?, ?>> world_map = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getMapList("CustomWorlds.Prices");
                for (Map<?, ?> m : world_map)
                    for (Map.Entry<?, ?> entry : m.entrySet()) {
                        String _world = entry.getKey().toString();
                        //System.out.println("Custom World Price " + _world + ":" + entry.getValue().toString());
                        if (entry.getValue().getClass() == Integer.class)
                            prices.put(_world, Integer.parseInt((entry.getValue().toString())));
                    }
            }
        } else
            price = 0;
        //Other
        this.Biomes = config.getStringList(pre + ".Biomes");
        this.miny = config.getInt(pre + ".MinY");
        if (miny > 0) {
            miny = 0;
            BetterRTP.getInstance().getLogger().warning("Warning! Default MinY value is solely for 1.17+ support, and can only be negative!");
        }
        this.maxy = config.getInt(pre + ".MaxY");
        if (maxy < 64) {
            maxy = 320;
            BetterRTP.getInstance().getLogger().warning("Warning! Default MaxY value is below water level (64)! Reset to default 320!");
        }
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
        return maxBorderRad;
    }

    @Override
    public int getMinRadius() {
        return minBorderRad;
    }

    public int getPrice(String world) {
        return prices.getOrDefault(world, getPrice());
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public List<String> getBiomes() {
        return Biomes;
    }

    @NotNull
    @Override
    public World getWorld() {
        return null;
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
}
