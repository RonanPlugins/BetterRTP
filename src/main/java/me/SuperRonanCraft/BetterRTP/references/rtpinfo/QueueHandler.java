package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseQueue;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class QueueHandler implements Listener { //Randomly queues up some safe locations

    boolean loaded = false;
    private final QueueGenerator generator = new QueueGenerator();

    public static boolean isEnabled() {
        return BetterRTP.getInstance().getSettings().isQueueEnabled();
    }

    public void registerEvents(BetterRTP pl) {
        PluginManager pm = pl.getServer().getPluginManager();
        pm.registerEvents(this, pl);
    }

    public void unload() {
        generator.unload();
    }

    public void load() {
        loaded = false;
        generator.load();
    }

    @EventHandler
    public void onRTP(RTP_TeleportPostEvent e) {
        //Delete previously used location
        remove(e.getLocation());
    }

    public static QueueData getRandomAsync(RTPWorld rtpWorld) {
        List<QueueData> queueData = getApplicableAsync(rtpWorld);
        if (queueData.size() <= QueueGenerator.queueMin && !BetterRTP.getInstance().getQueue().generator.generating)
            BetterRTP.getInstance().getQueue().generator.generate(rtpWorld);
        if (!queueData.isEmpty()) {
            QueueData randomQueue = queueData.get(new Random().nextInt(queueData.size()));
            queueData.clear();
            return randomQueue;
        }
        return null;
    }

    public static List<QueueData> getApplicableAsync(RTPWorld rtpWorld) {
        List<QueueData> available = new ArrayList<>();
        //Is Enabled??
        if (!isEnabled()) return available;
        List<QueueData> queueData = DatabaseHandler.getQueue().getInRange(new DatabaseQueue.QueueRangeData(rtpWorld));
        for (QueueData data : queueData) {
            if (!Objects.equals(data.getLocation().getWorld().getName(), rtpWorld.getWorld().getName()))
                continue;
            switch (rtpWorld.getShape()) {
                case CIRCLE:
                    if (isInCircle(data.location, rtpWorld))
                        available.add(data);
                    break;
                case SQUARE:
                default:
                    if (isInSquare(data.location, rtpWorld))
                        available.add(data);
            }
        }

        //BetterRTP.getInstance().getLogger().info("Centerx " + rtpWorld.getCenterX());
        //BetterRTP.getInstance().getLogger().info("Available: " + available.size());
        return available;
    }

    public static void remove(Location loc) {
        if (!isEnabled()) return;
        AsyncHandler.async(() -> {
            //Delete all queue data async
            if (DatabaseHandler.getQueue().removeLocation(loc)) {
                //BetterRTP.getInstance().getQueue().queueList.remove(data);
                BetterRTP.debug("-Removed a queue " + loc);
            }
        });
    }

    public static boolean isInCircle(Location loc, RTPWorld rtpWorld) {
        int center_x = rtpWorld.getCenterX();
        int center_z = rtpWorld.getCenterZ();
        int radius = rtpWorld.getMaxRadius();
        int radius_min = rtpWorld.getMinRadius();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int square_dist = (center_x - x) * 2 + (center_z - z) * 2;
        return square_dist <= radius * 2 && square_dist >= radius_min * 2;
    }

    public static boolean isInSquare(Location loc, RTPWorld rtpWorld) {
        int center_x = rtpWorld.getCenterX();
        int center_z = rtpWorld.getCenterZ();
        int radius_max = rtpWorld.getMinRadius();
        int radius_min = rtpWorld.getMinRadius();
        int x = loc.getBlockX() - center_x;
        int z = loc.getBlockZ() - center_z;
        return ((Math.abs(x)>=radius_min || Math.abs(z)>=radius_min) && (Math.abs(x) <= radius_max && Math.abs(z) <= radius_max));
        // Returns true if the x or z coordinate is above the MinRadius and if they are both under the MaxRadius. Returns false otherwise.
        // (All locations provided should be below the MaxRadius anyway, but I put it in just in-case.)
    }
}

