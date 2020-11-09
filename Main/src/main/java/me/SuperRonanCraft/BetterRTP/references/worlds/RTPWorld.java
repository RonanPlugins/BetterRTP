package me.SuperRonanCraft.BetterRTP.references.worlds;

import org.bukkit.World;

import java.util.List;

public interface RTPWorld {

    boolean getUseWorldborder();

    int getCenterX();

    int getCenterZ();

    int getMaxRad();

    int getMinRad();

    int getPrice();

    List<String> getBiomes();

    World getWorld();
}
