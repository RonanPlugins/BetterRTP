package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPlayer;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WORLD_TYPE;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldDefault;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QueueHandler implements Listener { //Randomly queues up some safe locations

    boolean loaded = false;
    List<QueueData> queueList = new ArrayList<>();
    private final int queueSize = 8; //Amount to ready up for each rtp world

    public void registerEvents(BetterRTP pl) {
        //DEBUG ONLY FOR THE TIME BEING
        if (!BetterRTP.getInstance().getSettings().isDebug())
            return;
        PluginManager pm = pl.getServer().getPluginManager();
        pm.registerEvents(this, pl);
    }

    public void load() {
        //DEBUG ONLY FOR THE TIME BEING
        if (!BetterRTP.getInstance().getSettings().isDebug())
            return;
        loaded = false;
        queueDownload();
    }

    private void queueDownload() {
        //LOAD FROM DATABASE
        Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {

            if (!DatabaseHandler.getQueue().isLoaded()) {
                queueDownload();
                return;
            }
            //Download all queue cached from last session
            queueList = DatabaseHandler.getQueue().getQueues();
            loaded = true;
        }, 10L);
    }

    //Inject queue to find event
    @EventHandler
    public void onRtpFindLoc(RTP_FindLocationEvent e) {
        List<QueueData> queueData = getApplicable(e.getWorld());
        if (!queueData.isEmpty()) {
            QueueData data = queueData.get(new Random().nextInt(queueData.size()));
            e.setLocation(data.location);
        }
        queueGenerator(e.getWorld());
    }

    @EventHandler
    public void onRTP(RTP_TeleportPostEvent e) {
        //Delete previously used location
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            Location location = e.getLocation();
            List<QueueData> deleteList = new ArrayList<>();
            for (QueueData data : queueList) {
                Location dataLoc = data.getLocation();
                if (location.getBlockX() == dataLoc.getBlockX()
                        && location.getBlockY() == dataLoc.getBlockY()
                        && location.getBlockZ() == dataLoc.getBlockZ()
                        && location.getWorld().getName().equals(dataLoc.getWorld().getName())) {
                    deleteList.add(data);
                }
            }
            deleteList.forEach(data -> queueList.remove(data));
        });
    }


    private void queueGenerator(RTPWorld rtpWorld) {
        /*if (queueList.size() >= queueSize) {
            //Plenty of locations, cancel out
            return;
        }*/

        Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {
            //Generate more locations
            //Rare cases where a rtp world didnt have a location generated (Permission Groups?)
            if (getApplicable(rtpWorld).size() < queueSize / 2) {
                generateFromWorld(rtpWorld);
                queueGenerator(rtpWorld); //Generate another later
                return;
            }

            //Generate Defaults
            WorldDefault worldDefault = BetterRTP.getInstance().getRTP().RTPdefaultWorld;
            for (World world : Bukkit.getWorlds()) {
                if (!BetterRTP.getInstance().getRTP().getDisabledWorlds().contains(world.getName())
                        && !BetterRTP.getInstance().getRTP().RTPcustomWorld.containsKey(world.getName())) {
                    if (getApplicable(worldDefault).size() < queueSize) {
                        generateFromWorld(worldDefault);
                        queueGenerator(null); //Generate another later
                        return;
                    }
                }
            }

            //Generate Custom Worlds
            for (Map.Entry<String, RTPWorld> customWorld : BetterRTP.getInstance().getRTP().RTPcustomWorld.entrySet()) {
                RTPWorld world = customWorld.getValue();
                if (getApplicable(world).size() < queueSize) {
                    generateFromWorld(world);
                    queueGenerator(null); //Generate another later
                    return;
                }
            }

            //Generate Locations
            for (Map.Entry<String, RTPWorld> location : BetterRTP.getInstance().getRTP().RTPworldLocations.entrySet()) {
                RTPWorld world = location.getValue();
                if (getApplicable(world).size() < queueSize) {
                    generateFromWorld(world);
                    queueGenerator(null); //Generate another later
                    return;
                }
            }
        }, 20L * 5 /*delay before starting queue generator*/);
    }

    //Generate a location on another thread
    private void generateFromWorld(RTPWorld world) {
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> addQueue(world, new QueueData(world)));
    }

    private void addQueue(RTPWorld rtpWorld, QueueData data) {
        Location loc = RTPPlayer.getSafeLocation(
                RTP.getWorldType(rtpWorld),
                data.getLocation().getWorld(),
                data.getLocation(),
                rtpWorld.getMinY(),
                rtpWorld.getMaxY(),
                rtpWorld.getBiomes());
        if (loc != null) {
            data.setLocation(loc);
            queueList.add(data);
        }
    }

    public static List<QueueData> getApplicable(RTPWorld rtpWorld) {
        List<QueueData> queueData = BetterRTP.getInstance().getQueue().queueList;
        List<QueueData> available = new ArrayList<>();
        for (QueueData data : queueData) {
            switch (rtpWorld.getShape()) {
                case CIRCLE:
                    if (isInCircle(data.location, rtpWorld))
                        available.add(data);
                    break;
                default:
                    if (isInSquare(data.location, rtpWorld))
                        available.add(data);
            }
        }
        return available;
    }

    private static boolean isInCircle(Location loc, RTPWorld rtpWorld) {
        int center_x = rtpWorld.getCenterX();
        int center_z = rtpWorld.getCenterZ();
        int radius = rtpWorld.getMaxRadius();
        int radius_min = rtpWorld.getMinRadius();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int square_dist = (center_x - x) * 2 + (center_z - z) * 2;
        return square_dist <= radius * 2 && square_dist >= radius_min * 2;
    }

    private static boolean isInSquare(Location loc, RTPWorld rtpWorld) {
        int center_x = rtpWorld.getCenterX();
        int center_z = rtpWorld.getCenterZ();
        int radius = rtpWorld.getMaxRadius();
        int radius_min = rtpWorld.getMinRadius();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        boolean xright = x <= center_x + radius && x >= center_x + radius_min;
        boolean xleft = x >= center_x - radius && x <= center_x - radius_min;
        if (!(xleft || xright))
            return false;
        boolean zbottom = z <= center_z + radius && z >= center_z + radius_min;
        boolean ztop = z >= center_z - radius && z <=center_z - radius_min;
        return ztop || zbottom;
    }
}

