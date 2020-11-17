package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds.WrapperPlayServerBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PortalsCache {

    private final HashMap<Player, PortalsCreation> portalsBeingCreated = new HashMap<>();

    void unload() {
        portalsBeingCreated.clear();
    }

    void uncache(Player p) {
        portalsBeingCreated.remove(p);
    }

    public void setPortal(Player p, Location loc, boolean loc2) {
        PortalsCreation portal;
        if (portalsBeingCreated.containsKey(p)) {
            portal = portalsBeingCreated.get(p);
        } else {
            portal = new PortalsCreation(p);
            portalsBeingCreated.put(p, portal);
        }
        Location old_loc1 = portal.loc_1;
        Location old_loc2 = portal.loc_1;
        if (loc2) portal.loc_2 = loc;
        else portal.loc_1 = loc;
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();
            WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange(pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE));
            block.setBlockData(WrappedBlockData.createData(Material.GLOWSTONE));
            block.setLocation(new BlockPosition(loc.toVector()));
            block.sendPacket(p);


        }

        if (portal.loc_1 != null && portal.loc_2 != null) {
            //if (Math.abs(portal.loc_1.getBlockX() - portal.loc_2.getBlockX()) <= 10)
            //if (Math.abs(portal.loc_1.getBlockZ() - portal.loc_2.getBlockZ()) <= 10)
            //if (Math.abs(portal.loc_1.getBlockY() - portal.loc_2.getBlockY()) <= 10) {
            preview(portal.loc_1, portal.loc_2);
            //}
        } else {
            p.sendMessage((portal.loc_1 == null) + " " + (portal.loc_2 == null));
        }
    }

    private void preview(Location loc1, Location loc2) {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            PacketContainer packet = pm
                    .createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            Chunk chunk = loc1.getChunk();
            ChunkCoordIntPair chunkcoords = new ChunkCoordIntPair(chunk.getX(),
                    chunk.getZ());
            MultiBlockChangeInfo[] change = new MultiBlockChangeInfo[256];


            for (int x = 0; x <= 15; x++) {
                for(int z = 0; z <= 15; z++) {
                    change[(16 * x) + z] = new MultiBlockChangeInfo(new Location(loc1.getWorld(), loc1.getX() + x, 100, loc1.getZ() + z), WrappedBlockData.createData(Material.GOLD_BLOCK));
                }
            }

            packet.getChunkCoordIntPairs().write(0, chunkcoords);
            packet.getMultiBlockChangeInfoArrays().write(0, change);


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

    public PortalsCreation getPortal(Player p) {
        return portalsBeingCreated.getOrDefault(p, null);
    }
}
