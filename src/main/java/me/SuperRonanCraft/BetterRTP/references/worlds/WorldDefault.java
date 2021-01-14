package me.SuperRonanCraft.BetterRTP.references.worlds;


import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import scala.Int;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldDefault implements RTPWorld {
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price;
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
            BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Default Maximum radius of '" + maxBorderRad + "' is not allowed! Set to '1000'");
            maxBorderRad = 1000;
        }
        minBorderRad = config.getInt(pre + ".MinRadius");
        if (minBorderRad < 0 || minBorderRad >= maxBorderRad) {
            BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Default Minimum radius of '" + minBorderRad + "' is not allowed! Set to '0'");
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

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public RTP_SHAPE getShape() {
        return shape;
    }
}
