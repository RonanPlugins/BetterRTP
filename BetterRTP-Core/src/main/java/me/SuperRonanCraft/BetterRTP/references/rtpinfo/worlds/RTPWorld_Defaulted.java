package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import org.bukkit.World;

import java.util.List;

public interface RTPWorld_Defaulted {

    void setUseWorldBorder(boolean value);

    void setCenterX(int value);

    void setCenterZ(int value);

    void setMaxRadius(int value);

    void setMinRadius(int value);

    void setPrice(int value);

    void setBiomes(List<String> value);

    void setWorld(World value);

    void setShape(RTP_SHAPE value);

    void setMinY(int value);

    void setMaxY(int value);

    void setCooldown(long value);

    void setRTPOnDeath(boolean bool);

    default void setupDefaults() {
        setAllFrom(BetterRTP.getInstance().getRTP().getRTPdefaultWorld());
    }

    default void setAllFrom(RTPWorld rtpWorld) {
        setMaxRadius(rtpWorld.getMaxRadius());
        setMinRadius(rtpWorld.getMinRadius());
        setUseWorldBorder(rtpWorld.getUseWorldborder());
        setCenterX(rtpWorld.getCenterX());
        setCenterZ(rtpWorld.getCenterZ());
        setPrice(rtpWorld.getPrice());
        setBiomes(rtpWorld.getBiomes());
        setShape(rtpWorld.getShape());
        setMinY(rtpWorld.getMinY());
        setMaxY(rtpWorld.getMaxY());
        setCooldown(rtpWorld.getCooldown());
        setRTPOnDeath(rtpWorld.getRTPOnDeath());
    }
}
