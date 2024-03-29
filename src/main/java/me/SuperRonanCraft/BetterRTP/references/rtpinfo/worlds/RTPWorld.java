package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import java.util.List;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;

public interface RTPWorld {

    boolean getUseWorldborder();

    int getCenterX();

    int getCenterZ();

    int getMaxRadius();

    int getMinRadius();

    int getPrice();

    List<String> getBiomes();

    @NonNull World getWorld();

    RTP_SHAPE getShape();

    int getMinY();

    int getMaxY();

    @Nullable
    default String getID() {
        return null;
    }

    long getCooldown();

    boolean getRTPOnDeath();
}
