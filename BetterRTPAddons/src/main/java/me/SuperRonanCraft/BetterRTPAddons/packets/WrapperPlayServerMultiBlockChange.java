package me.SuperRonanCraft.BetterRTPAddons.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;

/**
 * Represents a multi-block change.
 * <p>
 * See also {@link BlockChangeArray} for constructing an array of block changes.
 *
 * @author Kristian
 */
public class WrapperPlayServerMultiBlockChange extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MULTI_BLOCK_CHANGE;

    public WrapperPlayServerMultiBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMultiBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the chunk x position.
     * @return The chunk x.
     */
    public int getChunkX() {
        return getChunk().getChunkX();
    }

    /**
     * Set the chunk x position.
     * @param index - the new position.
     */
    public void setChunkX(int index) {
        setChunk(new ChunkCoordIntPair(index, getChunkZ()));
    }

    /**
     * Retrieve the chunk Z position.
     * @return The chunk z.
     */
    public int getChunkZ() {
        return getChunk().getChunkZ();
    }

    /**
     * Set the chunk Z position.
     * @param index - the new position.
     */
    public void setChunkZ(int index) {
        setChunk(new ChunkCoordIntPair(getChunkX(), index));
    }

    /**
     * Retrieve the chunk that has been altered.
     * @return The current chunk
     */
    public ChunkCoordIntPair getChunk() {
        return handle.getChunkCoordIntPairs().read(0);
    }

    /**
     * Set chunk that has been altered.
     * @param value - new value.
     */
    public void setChunk(ChunkCoordIntPair value) {
        handle.getChunkCoordIntPairs().write(0, value);
    }

    /**
     * Retrieve the number of blocks affected.
     * @return The current Record count
     */
    public short getRecordCount() {
        return handle.getIntegers().read(0).shortValue();
    }

    /**
     * Set the number of blocks affected.
     * @param value - new value.
     */
    public void setRecordCount(short value) {
        handle.getIntegers().write(0, (int) value);
    }

    /**
     * Retrieve the record data.
     * <p>
     * Each record is four bytes, containing the following data:
     * <table border="1" cellpadding="3">
     *  <tr>
     *   <th>Bit mask</th>
     *   <th>Width</th>
     *   <th>Meaning</th>
     *  </tr>
     *  <tr>
     *   <td>00 00 00 0F</td>
     *   <td>4 bits</td>
     *   <td>Block metadata</td>
     *  </tr>
     *  <tr>
     *   <td>00 00 FF F0</td>
     *   <td>12 bits</td>
     *   <td>Block ID</td>
     *  </tr>
     *  <tr>
     *   <td>00 FF 00 00</td>
     *   <td>8 bits</td>
     *   <td>Y co-ordinate</td>
     *  </tr>
     *  <tr>
     *   <td>0F 00 00 00</td>
     *   <td>4 bits</td>
     *   <td>Z co-ordinate, relative to chunk </td>
     *  </tr>
     *  <tr>
     *   <td>F0 00 00 00 </td>
     *   <td>4 bits</td>
     *   <td>X co-ordinate, relative to chunk</td>
     *  </tr>
     * </table>
     *
     * @return The current Record count
     */
    public byte[] getRecordData() {
        return handle.getByteArrays().read(0);
    }

    /**
     * Set the record data.
     * <p>
     * Each record is four bytes. See {@link #getRecordData()} for more information.
     * @param value - new value.
     */
    public void setRecordData(byte[] value) {
        setRecordCount((short) value.length);
        handle.getByteArrays().write(0, value);
    }

    /**
     * Set the record data using the given helper array.
     * @param array - useful helper array.
     */
    public void setRecordData(BlockChangeArray array) {
        setRecordData(array.toByteArray());
    }

    /**
     * Retrieve a copy of the record data as a block change array.
     * @return The copied block change array.
     */
    public BlockChangeArray getRecordDataArray() {
        return new BlockChangeArray(getRecordData());
    }
}