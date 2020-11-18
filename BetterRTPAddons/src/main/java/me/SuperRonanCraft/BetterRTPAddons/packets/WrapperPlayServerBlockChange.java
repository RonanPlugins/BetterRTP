package me.SuperRonanCraft.BetterRTPAddons.packets;

import org.bukkit.Location;
import org.bukkit.World;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;

public class WrapperPlayServerBlockChange extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_CHANGE;

    public WrapperPlayServerBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Location.
     * <p>
     * Notes: block Coordinates
     *
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }

    /**
     * Set Location.
     *
     * @param value - new value.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }

    /**
     * Retrieve the Bukkit Location.
     *
     * @param world World for the location
     * @return Bukkit Location
     */
    public Location getBukkitLocation(World world) {
        return getLocation().toVector().toLocation(world);
    }

    /**
     * Retrieve Block Data.
     *
     * @return The current Block Data
     */
    public WrappedBlockData getBlockData() {
        return handle.getBlockData().read(0);
    }

    /**
     * Set Block Data.
     *
     * @param value - new value.
     */
    public void setBlockData(WrappedBlockData value) {
        handle.getBlockData().write(0, value);
    }
}