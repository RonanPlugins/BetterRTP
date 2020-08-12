package me.SuperRonanCraft.BetterRTP.references.worlds;


import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;

public class Default implements RTPWorld {
    private boolean useWorldborder;
    private int CenterX, CenterZ, maxBorderRad, minBorderRad, price;
    private List<String> Biomes;

    public void setup() {
        //Setups
        String pre = "Default";
        FileBasics.FILETYPE config = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        //Booleans
        useWorldborder = config.getBoolean(pre + ".UseWorldBorder");
        //Integers
        CenterX = config.getInt(pre + ".CenterX");
        CenterZ = config.getInt(pre + ".CenterZ");
        maxBorderRad = config.getInt(pre + ".MaxRadius");
        if (maxBorderRad <= 0) {
            Main.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Default Maximum radius of '" + maxBorderRad + "' is not allowed! Set to '1000'");
            maxBorderRad = 1000;
        }
        minBorderRad = config.getInt(pre + ".MinRadius");
        if (minBorderRad < 0 || minBorderRad >= maxBorderRad) {
            Main.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Default Minimum radius of '" + minBorderRad + "' is not allowed! Set to '0'");
            minBorderRad = 0;
        }
        if (Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
            price = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getInt("Economy.Price");
        else
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
}
