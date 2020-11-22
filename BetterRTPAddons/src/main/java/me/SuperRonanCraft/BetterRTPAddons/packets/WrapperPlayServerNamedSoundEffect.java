package me.SuperRonanCraft.BetterRTPAddons.packets;

import org.bukkit.Sound;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.SoundCategory;

public class WrapperPlayServerNamedSoundEffect extends AbstractPacket {
    public static final PacketType TYPE =
            PacketType.Play.Server.NAMED_SOUND_EFFECT;

    public WrapperPlayServerNamedSoundEffect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerNamedSoundEffect(PacketContainer packet) {
        super(packet, TYPE);
    }

    public Sound getSoundEffect() {
        return handle.getSoundEffects().read(0);
    }

    public void setSoundEffect(Sound value) {
        handle.getSoundEffects().write(0, value);
    }

    public SoundCategory getSoundCategory() {
        return handle.getSoundCategories().read(0);
    }

    public void setSoundCategory(SoundCategory value) {
        handle.getSoundCategories().write(0, value);
    }

    /**
     * Retrieve Effect position X.
     * <p>
     * Notes: effect X multiplied by 8
     *
     * @return The current Effect position X
     */
    public int getEffectPositionX() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Effect position X.
     *
     * @param value - new value.
     */
    public void setEffectPositionX(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve Effect position Y.
     * <p>
     * Notes: effect Y multiplied by 8
     *
     * @return The current Effect position Y
     */
    public int getEffectPositionY() {
        return handle.getIntegers().read(1);
    }

    /**
     * Set Effect position Y.
     *
     * @param value - new value.
     */
    public void setEffectPositionY(int value) {
        handle.getIntegers().write(1, value);
    }

    /**
     * Retrieve Effect position Z.
     * <p>
     * Notes: effect Z multiplied by 8
     *
     * @return The current Effect position Z
     */
    public int getEffectPositionZ() {
        return handle.getIntegers().read(2);
    }

    /**
     * Set Effect position Z.
     *
     * @param value - new value.
     */
    public void setEffectPositionZ(int value) {
        handle.getIntegers().write(2, value);
    }

    /**
     * Retrieve Volume.
     * <p>
     * Notes: 1 is 100%, can be more
     *
     * @return The current Volume
     */
    public float getVolume() {
        return handle.getFloat().read(0);
    }

    /**
     * Set Volume.
     *
     * @param value - new value.
     */
    public void setVolume(float value) {
        handle.getFloat().write(0, value);
    }

    /**
     * Retrieve Pitch.
     * <p>
     * Notes: 63 is 100%, can be more
     *
     * @return The current Pitch
     */
    public float getPitch() {
        return handle.getFloat().read(1);
    }

    /**
     * Set Pitch.
     *
     * @param value - new value.
     */
    public void setPitch(float value) {
        handle.getFloat().write(1, value);
    }

}
