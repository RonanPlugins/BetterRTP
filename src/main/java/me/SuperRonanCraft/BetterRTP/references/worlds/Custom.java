package me.SuperRonanCraft.BetterRTP.references.worlds;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Custom implements RTPWorld {
    public String world, world_type;
    private boolean useWorldborder = false;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price;
    private List<String> Biomes;

    public Custom(String world) {
        String pre = "CustomWorlds.";
        FileBasics.FILETYPE config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        List<Map<?, ?>> map = config.getMapList("CustomWorlds");
        this.world = world;

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
                    if (test.get("CenterZ").getClass() == Integer.class)
                        CenterZ = Integer.parseInt((test.get("CenterZ")).toString());
                }
                if (test.get("MaxRadius") != null) {
                    if (test.get("MaxRadius").getClass() == Integer.class)
                        maxBorderRad = Integer.parseInt((test.get("MaxRadius")).toString());
                    if (maxBorderRad <= 0) {
                        Main.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Custom world '" + world + "' Maximum radius of '" + maxBorderRad + "' is not allowed! Set to default value!");
                        maxBorderRad = Main.getInstance().getRTP().defaultWorld.getMaxRad();
                    }
                }
                if (test.get("MinRadius") != null) {
                    if (test.get("MinRadius").getClass() == Integer.class)
                        minBorderRad = Integer.parseInt((test.get("MinRadius")).toString());
                    if (minBorderRad < 0 || minBorderRad >= maxBorderRad) {
                        Main.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Custom world '" + world + "' Minimum radius of '" + minBorderRad + "' is not allowed! Set to default value!");
                        minBorderRad = Main.getInstance().getRTP().defaultWorld.getMinRad();
                        if (minBorderRad >= maxBorderRad)
                            maxBorderRad = Main.getInstance().getRTP().defaultWorld.getMaxRad();
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
        maxBorderRad = config.getInt(pre + world + ".MaxRadius");
        if (maxBorderRad <= 0) {
            Main.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Custom world '" + world + "' Maximum radius of '" + maxBorderRad + "' is not allowed! Set to default value!");
            maxBorderRad = Main.getInstance().getRTP().Default.getMaxRad();
        }
        minBorderRad = config.getInt(pre + world + ".MinRadius");
        if (minBorderRad <= 0 || minBorderRad >= maxBorderRad) {
            Main.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Custom world '" + world + "' Minimum radius of '" + minBorderRad + "' is not allowed! Set to default value!");
            minBorderRad = Main.getInstance().getRTP().Default.getMinRad();
        }
        */
        if (Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
            if (Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean(pre + "Enabled")) {
                map.clear();
                map = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getMapList("CustomWorlds");
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
                price = Main.getInstance().getRTP().defaultWorld.getPrice();
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
