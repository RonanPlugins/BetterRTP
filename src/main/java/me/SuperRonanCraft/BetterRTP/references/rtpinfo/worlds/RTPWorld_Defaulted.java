package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;

import java.util.List;

public interface RTPWorld_Defaulted {

    void setUseWorldBorder(boolean value);

    void setCenterX(int value);

    void setCenterZ(int value);

    void setMaxRad(int value);

    void setMinRad(int value);

    void setPrice(int value);

    void setBiomes(List<String> value);

    void setWorld(String value);

    void setShape(RTP_SHAPE value);

    default void setupDefaults() {
        WorldDefault worldDefault = BetterRTP.getInstance().getRTP().defaultWorld;
        setMaxRad(worldDefault.getMaxRad());
        setMinRad(worldDefault.getMinRad());
        setUseWorldBorder(worldDefault.getUseWorldborder());
        setCenterX(worldDefault.getCenterX());
        setCenterZ(worldDefault.getCenterZ());
        setPrice(worldDefault.getPrice());
        setBiomes(worldDefault.getBiomes());
        setShape(worldDefault.getShape());
    }
}
