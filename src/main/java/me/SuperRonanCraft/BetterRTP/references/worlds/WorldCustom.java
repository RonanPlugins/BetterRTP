package me.SuperRonanCraft.BetterRTP.references.worlds;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldCustom implements RTPWorld {
    public String world;
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price;
    private List<String> Biomes;

    public WorldCustom(String world) {
        String pre = "CustomWorlds.";
        FileBasics.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        List<Map<?, ?>> map = config.getMapList("CustomWorlds");
        this.world = world;

        //Set Defaults
        WorldDefault worldDefault = BetterRTP.getInstance().getRTP().defaultWorld;
        maxBorderRad = worldDefault.getMaxRad();
        minBorderRad = worldDefault.getMinRad();
        useWorldborder = worldDefault.getUseWorldborder();
        CenterX = worldDefault.getCenterX();
        CenterZ = worldDefault.getCenterZ();
        price = worldDefault.getPrice();
        Biomes = worldDefault.getBiomes();

        //Find Custom World and cache values
        for (Map<?, ?> m : map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                String key = entry.getKey().toString();
                if (!key.equals(world))
                    continue;
                Map<?, ?> test = ((Map<?, ?>) m.get(key));
                if (test == null)
                    continue;
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
                                "WARNING! Custom world '" + world + "' Maximum radius of '" + maxBorderRad + "' is not allowed! Set to default value!");
                        maxBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMaxRad();
                    }
                }
                if (test.get("MinRadius") != null) {
                    if (test.get("MinRadius").getClass() == Integer.class)
                        minBorderRad = Integer.parseInt((test.get("MinRadius")).toString());
                    if (minBorderRad < 0 || minBorderRad >= maxBorderRad) {
                        BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Custom world '" + world + "' Minimum radius of '" + minBorderRad + "' is not allowed! Set to default value!");
                        minBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMinRad();
                        if (minBorderRad >= maxBorderRad)
                            maxBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMaxRad();
                    }
                }
                if (test.get("Biomes") != null) {
                    if (test.get("Biomes").getClass() == ArrayList.class)
                        this.Biomes = new ArrayList<>((ArrayList) test.get("Biomes"));
                }
            }
        }
        //Booleans
        /*useWorldborder = config.getBoolean(pre + world + ".UseWorldBorder");
        //Integers
        CenterX = config.getInt(pre + world + ".CenterX");
        CenterZ = config.getInt(pre + world + ".CenterZ");
        maxBorderRad = config.getInt(pre + world + ".MaxRadius");*/
        if (maxBorderRad <= 0) {
            BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Custom world '" + world + "' Maximum radius of '" + maxBorderRad + "' is not allowed! Set to default value!");
            maxBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMaxRad();
        }
        //minBorderRad = config.getInt(pre + world + ".MinRadius");
        if (minBorderRad <= 0 || minBorderRad >= maxBorderRad) {
            BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Custom world '" + world + "' Minimum radius of '" + minBorderRad + "' is not allowed! Set to default value!");
            minBorderRad = BetterRTP.getInstance().getRTP().defaultWorld.getMinRad();
        }
        if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
            if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean(pre + "Enabled")) {
                map.clear();
                map = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getMapList("CustomWorlds");
                for (Map<?, ?> m : map)
                    for (Map.Entry<?, ?> entry : m.entrySet()) {
                        String key = entry.getKey().toString();
                        Map<?, ?> test = ((Map<?, ?>) m.get(key));
                        if (!key.equals(world))
                            continue;
                        if (test.get("Price") != null)
                            if (test.get("Price").getClass() == Integer.class)
                            price = Integer.valueOf((test.get("Price")).toString());
                    }
            } else
                price = BetterRTP.getInstance().getRTP().defaultWorld.getPrice();
        //Other
        this.Biomes = config.getStringList(pre + world + ".Biomes");
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
    public World getWorld() {
        return Bukkit.getWorld(world);
    }
}
