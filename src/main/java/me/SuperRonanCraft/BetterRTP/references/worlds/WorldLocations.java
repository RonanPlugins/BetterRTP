package me.SuperRonanCraft.BetterRTP.references.worlds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPermissionGroup;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldLocations implements RTPWorld {
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price;
    private List<String> Biomes;
    private String world;
    private RTP_SHAPE shape;

    public WorldLocations(String location_name) {
        FileBasics.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.LOCATIONS);
        List<Map<?, ?>> map = config.getMapList("Locations");
        WorldDefault worldDefault = BetterRTP.getInstance().getRTP().defaultWorld;

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
                    if (test.get("World").getClass() == String.class)
                        world = test.get("World").toString();
                } else {
                    BetterRTP.getInstance().getLogger().warning("Location `" + location_name + "` does NOT have a World specified!");
                    return;
                }
                if (test.get("UseWorldBorder") != null) {
                    if (test.get("UseWorldBorder").getClass() == Boolean.class)
                        useWorldborder = Boolean.parseBoolean(test.get("UseWorldBorder").toString());
                }
                if (test.get("CenterX") != null) {
                    if (test.get("CenterX").getClass() == Integer.class)
                        CenterX = Integer.parseInt((test.get("CenterX")).toString());
                }
                if (test.get("CenterZ") != null) {
                    if (test.get("CenterZ").getClass() == Integer.class) {
                        CenterZ = Integer.parseInt((test.get("CenterZ")).toString());
                    }
                }
                if (test.get("MaxRadius") != null) {
                    if (test.get("MaxRadius").getClass() == Integer.class)
                        maxBorderRad = Integer.parseInt((test.get("MaxRadius")).toString());
                    if (maxBorderRad <= 0) {
                        BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Location '" + location_name + "' Maximum radius of '" + maxBorderRad + "' is not allowed! Set to default value!");
                        maxBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMaxRad();
                    }
                }
                if (test.get("MinRadius") != null) {
                    if (test.get("MinRadius").getClass() == Integer.class)
                        minBorderRad = Integer.parseInt((test.get("MinRadius")).toString());
                    if (minBorderRad < 0 || minBorderRad >= maxBorderRad) {
                        BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Location '" + location_name + "' Minimum radius of '" + minBorderRad + "' is not allowed! Set to default value!");
                        minBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMinRad();
                        if (minBorderRad >= maxBorderRad)
                            maxBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMaxRad();
                    }
                }
                if (test.get("Biomes") != null) {
                    if (test.get("Biomes").getClass() == ArrayList.class)
                        this.Biomes = new ArrayList<String>((ArrayList) test.get("Biomes"));
                }
                if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
                    if (test.get("Price") != null) {
                        if (test.get("Price").getClass() == Integer.class)
                            this.price = Integer.parseInt(test.get("Price").toString());
                        else
                            price = worldDefault.getPrice(world);
                    } else
                        price = worldDefault.getPrice(world);
                if (test.get("Shape") != null) {
                    if (test.get("Shape").getClass() == String.class) {
                        try {
                            this.shape = RTP_SHAPE.valueOf(test.get("Shape").toString().toUpperCase());
                        } catch (Exception e) {
                            //Invalid shape
                        }
                    }
                }
            }
        }
    }

    public boolean isValid() {
        return world != null;
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(world);
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

    @Override
    public List<String> getBiomes() {
        return Biomes;
    }

    @Override
    public RTP_SHAPE getShape() {
        return shape;
    }
}
