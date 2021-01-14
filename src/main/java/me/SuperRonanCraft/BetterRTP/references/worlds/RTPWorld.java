package me.SuperRonanCraft.BetterRTP.references.worlds;

import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
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

    RTP_SHAPE getShape();
}
