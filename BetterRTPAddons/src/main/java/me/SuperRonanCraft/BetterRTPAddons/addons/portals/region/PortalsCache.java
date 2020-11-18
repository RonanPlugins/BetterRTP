package me.SuperRonanCraft.BetterRTPAddons.addons.portals.region;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.packets.BlockChangeArray;
import me.SuperRonanCraft.BetterRTPAddons.packets.WrapperPlayServerBlockChange;
import me.SuperRonanCraft.BetterRTPAddons.packets.WrapperPlayServerMultiBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class PortalsCache {

    AddonPortals addonPortals;
    private List<PortalsRegionInfo> registeredPortals;
    private final HashMap<Player, PortalsRegionInfo> portalsBeingCreated = new HashMap<>();

    public PortalsCache(AddonPortals addonPortals) {
        this.addonPortals = addonPortals;
    }

    public void load() {
        registeredPortals = addonPortals.getDatabase().getPortals();
    }

    public void unload() {
        portalsBeingCreated.clear();
    }

    void uncache(Player p) {
        portalsBeingCreated.remove(p);
    }

    public List<PortalsRegionInfo> getRegisteredPortals() {
        return registeredPortals;
    }

    public boolean removeRegisteredPortal(PortalsRegionInfo portal) {
        registeredPortals.remove(portal);
        return addonPortals.getDatabase().removePortal(portal);
    }

    public boolean setPortal(Player p, String name) {
        if (!portalsBeingCreated.containsKey(p))
            return false;
        PortalsRegionInfo portal = portalsBeingCreated.get(p);
        portal.name = name;
        return addonPortals.getDatabase().setPortal(portal);
    }

    public void cachePortal(Player p, Location loc, boolean loc2) {
        PortalsRegionInfo portal;
        if (portalsBeingCreated.containsKey(p)) {
            portal = portalsBeingCreated.get(p);
        } else {
            portal = new PortalsRegionInfo();
            portalsBeingCreated.put(p, portal);
        }
        Location old_loc1 = portal.loc_1;
        Location old_loc2 = portal.loc_1;
        if (loc2)
            portal.loc_2 = loc;
        else
            portal.loc_1 = loc;
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();

            if (portal.loc_1 != null && portal.loc_2 != null) {
                //if (Math.abs(portal.loc_1.getBlockX() - portal.loc_2.getBlockX()) <= 10)
                //if (Math.abs(portal.loc_1.getBlockZ() - portal.loc_2.getBlockZ()) <= 10)
                //if (Math.abs(portal.loc_1.getBlockY() - portal.loc_2.getBlockY()) <= 10) {
                //preview(portal.loc_1, portal.loc_2);
                //}
                /*Location max = portal.loc_1;
                Location min = portal.loc_2;
                for (int x = Math.max(max.getBlockX(), min.getBlockX()); x >= Math.min(min.getBlockX(), max.getBlockX()); x--) {
                    for (int y = Math.max(max.getBlockY(), min.getBlockY()); y >= Math.min(min.getBlockY(), max.getBlockY()); y--) {
                        for (int z = Math.max(max.getBlockZ(), min.getBlockZ()); z >= Math.min(min.getBlockZ(), max.getBlockZ()); z--) {
                            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE));
                            packet.setBlockData(WrappedBlockData.createData(Material.REDSTONE_BLOCK));
                            packet.setLocation(new BlockPosition(new Location(max.getWorld(), x, y, z).toVector()));
                            packet.sendPacket(p);
                            //Block block = max.getWorld().getBlockAt(x, y, z);
                            //block.setType(Material.GLOWSTONE);
                        }
                    }
                }*/
            } else {
                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE));
                packet.setBlockData(WrappedBlockData.createData(Material.GLOWSTONE));
                packet.setLocation(new BlockPosition(loc.toVector()));
                packet.sendPacket(p);
            }
        }
    }

    private void preview(Location loc1, Location loc2) {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            PacketContainer packet = pm
                    .createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            Chunk chunk = loc1.getChunk();
            BlockChangeArray change = new BlockChangeArray(256);

            WrapperPlayServerMultiBlockChange wrapper = new WrapperPlayServerMultiBlockChange(packet);
            wrapper.setChunkX(chunk.getX());
            wrapper.setChunkZ(chunk.getZ());
            for (int x = 0; x <= 15; x++) {
                for(int z = 0; z <= 15; z++) {
                    int index = (16 * x) + z;
                    BlockChangeArray.BlockChange block = change.getBlockChange(index);
                    block.setBlockID(WrappedBlockData.createData(Material.GOLD_BLOCK).getType().getId());
                    Location loc = loc1.clone();
                    loc.setX(loc.getX() + x);
                    loc.setZ(loc.getZ() + z);
                    block.setLocation(loc);
                    change.setBlockChange(index, block);
                }
            }

            wrapper.setRecordData(change);
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    pm.sendServerPacket(player, packet);
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    public PortalsRegionInfo getPortal(Player p) {
        return portalsBeingCreated.getOrDefault(p, null);
    }
}
