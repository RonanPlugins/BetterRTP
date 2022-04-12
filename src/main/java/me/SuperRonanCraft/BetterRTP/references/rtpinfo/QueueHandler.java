package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPlayer;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldCustom;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldDefault;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class QueueHandler implements Listener { //Randomly queues up some safe locations

    boolean loaded = false;
    List<QueueData> queueList = new ArrayList<>();
    private final int queueMax = 32, queueMin = 2; //Amount to ready up for each rtp world
    private final int queueMaxAttempts = 50;
    private boolean generating;
    private BukkitTask task;


    public void registerEvents(BetterRTP pl) {
        PluginManager pm = pl.getServer().getPluginManager();
        pm.registerEvents(this, pl);
    }

    public void load() {
        loaded = false;
        if (task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()))
            task.cancel();

        if (!BetterRTP.getInstance().getSettings().isQueueEnabled())
            return;
        queueDownload();
    }

    private void queueDownload() {
        queueList.clear();
        //LOAD FROM DATABASE
        Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {

            if (!DatabaseHandler.getQueue().isLoaded()) {
                queueDownload();
                return;
            }
            //Download all queue cached from last session
            queueList = DatabaseHandler.getQueue().getQueues();
            loaded = true;
            BetterRTP.debug("Loaded " + queueList.size() + " previously generated safe locations!");
            //Queue after everything was loaded
            BetterRTP.debug("Attempting to queue up some more safe locations...");
            queueGenerator(null, queueMax, queueMin, 0, "noone", 0);
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
        if (!generating)
            queueGenerator(e.getWorld(), queueMax, queueMin, 0, "noone", 0);
    }

    @EventHandler
    public void onRTP(RTP_TeleportPostEvent e) {
        //Delete previously used location
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            Location location = e.getLocation();
            List<QueueData> deleteList = new ArrayList<>();
            for (QueueData data : queueList) {
                Location dataLoc = data.getLocation();
                //BetterRTP.debug("--");
                //BetterRTP.debug(location.getBlockX() + " -> " + dataLoc.getBlockX());
                //BetterRTP.debug(location.getBlockZ() + " -> " + dataLoc.getBlockZ());
                if (location.getBlockX() == dataLoc.getBlockX()
                        && location.getBlockZ() == dataLoc.getBlockZ()
                        && location.getWorld().getName().equals(dataLoc.getWorld().getName())) {
                    deleteList.add(data);
                }
            }
            deleteList.forEach(data -> {
                if (DatabaseHandler.getQueue().removeQueue(data)) {
                    queueList.remove(data);
                    BetterRTP.debug("-Removed a queue " + data.getLocation().toString());
                }
            });
        });
    }


    private void queueGenerator(RTPWorld rtpWorld, int queueMax, int queueMin, int lastCount, @NonNull String lastType, int attempts) {
        /*if (queueList.size() >= queueSize) {
            //Plenty of locations, cancel out
            return;
        }*/
        generating = true;
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {
            //Generate more locations
            //Rare cases where a rtp world didnt have a location generated (Permission Groups?)
            if (rtpWorld != null) {
                List<QueueData> applicable = getApplicable(rtpWorld);
                String type = "superCustom_" + (rtpWorld.getID() != null ? rtpWorld.getID() : rtpWorld.getWorld().getName());
                int newCount = lastType.equalsIgnoreCase(type) ? lastCount : applicable.size();
                int attempt = lastType.equalsIgnoreCase(type) ? attempts + 1 : 0;
                if (newCount < queueMin && applicable.size() < queueMax) {
                    if (attempt > queueMaxAttempts) {
                        BetterRTP.debug("Max attempts to create a Queue reached for " + type + " (amount: " + applicable.size() + ")");
                        return;
                    }
                    generateFromWorld(rtpWorld);
                    queueGenerator(rtpWorld, queueMax, queueMin, newCount, type, attempt); //Generate another later
                    return;
                }
                if (lastType.equalsIgnoreCase(type))
                    BetterRTP.debug("Queue max reached for " + type + " (amount: " + applicable.size() + ") lastCount: " + lastCount);
            }

            //Generate Defaults
            WorldDefault worldDefault = BetterRTP.getInstance().getRTP().RTPdefaultWorld;
            for (World world : Bukkit.getWorlds()) {
                if (!BetterRTP.getInstance().getRTP().getDisabledWorlds().contains(world.getName())
                        && !BetterRTP.getInstance().getRTP().RTPcustomWorld.containsKey(world.getName())) {
                    RTPWorld newWorld = new WorldCustom(world, worldDefault);
                    List<QueueData> applicable = getApplicable(newWorld);
                    String type = "default_" + world.getName();
                    int newCount = lastType.equalsIgnoreCase(type) ? lastCount : applicable.size();
                    int attempt = lastType.equalsIgnoreCase(type) ? attempts + 1 : 0;
                    if (newCount < queueMin && applicable.size() < queueMax) {
                        if (attempt > queueMaxAttempts) {
                            BetterRTP.debug("Max attempts to create a Queue reached for " + type + " (amount: " + applicable.size() + ") world: " + world.getName());
                            continue;
                        }
                        generateFromWorld(newWorld);
                        queueGenerator(null, queueMax, queueMin, newCount, type, attempt); //Generate another later
                        return;
                    }
                    if (lastType.equalsIgnoreCase(type))
                        BetterRTP.debug("Queue max reached for " + type + " (amount: " + applicable.size() + ") world: " + world.getName() + " lastCount: " + lastCount);
                }
            }

            //Generate Custom Worlds
            for (Map.Entry<String, RTPWorld> customWorld : BetterRTP.getInstance().getRTP().RTPcustomWorld.entrySet()) {
                RTPWorld world = customWorld.getValue();
                List<QueueData> applicable = getApplicable(world);
                String type = "custom_" + customWorld.getKey();
                int newCount = lastType.equalsIgnoreCase(type) ? lastCount : applicable.size();
                int attempt = lastType.equalsIgnoreCase(type) ? attempts + 1 : 0;
                if (newCount < queueMin && applicable.size() < queueMax) {
                    if (attempt > queueMaxAttempts) {
                        BetterRTP.debug("Max attempts to create a Queue reached for " + type + " (amount:" + applicable.size() + ") " + customWorld.getValue().getWorld().getName());
                        continue;
                    }
                    generateFromWorld(world);
                    queueGenerator(null, queueMax, queueMin, newCount, type, attempt); //Generate another later
                    return;
                }
                if (lastType.equalsIgnoreCase(type))
                    BetterRTP.debug("Queue max reached for " + type + " (amount: " + applicable.size() + ") " + customWorld.getValue().getWorld().getName() + " lastCount: " + lastCount);
            }

            //Generate Locations
            for (Map.Entry<String, RTPWorld> location : BetterRTP.getInstance().getRTP().RTPworldLocations.entrySet()) {
                RTPWorld world = location.getValue();
                List<QueueData> applicable = getApplicable(world);
                String type = "location_" + location.getValue().getID();
                int newCount = lastType.equalsIgnoreCase(type) ? lastCount : applicable.size();
                int attempt = lastType.equalsIgnoreCase(type) ? attempts + 1 : 0;
                if (newCount < queueMin && applicable.size() < queueMax) {
                    if (attempt > queueMaxAttempts) {
                        BetterRTP.debug("Max attempts to create a Queue reached for " + type + " " + applicable.size() + " " + location.getValue().getID());
                        continue;
                    }
                    generateFromWorld(world);
                    queueGenerator(null, queueMax, queueMin, newCount, type, attempt); //Generate another later
                    return;
                }
                if (lastType.equalsIgnoreCase(type))
                    BetterRTP.debug("Queue max reached for " + type + " " + applicable.size() + " " + location.getValue().getID() + " lastCount: " + lastCount);
            }
            generating = false;
            BetterRTP.debug("Queueing paused, max queue limit reached!");
        }, 20L * 5 /*delay before starting queue generator*/);
    }

    //Generate a location on another thread
    private void generateFromWorld(RTPWorld world) {
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            //BetterRTP.debug("Queue attempt started...");
            QueueData data = new QueueData(world);
            addQueue(world, data);
        });
    }

    private void addQueue(RTPWorld rtpWorld, QueueData data) {
        Location loc = null;
        if (data.getLocation() != null)
            loc = RTPPlayer.getSafeLocation(
                RTP.getWorldType(rtpWorld),
                data.getLocation().getWorld(),
                data.getLocation(),
                rtpWorld.getMinY(),
                rtpWorld.getMaxY(),
                rtpWorld.getBiomes());
        if (loc != null) {
            data.setLocation(loc);
            if (DatabaseHandler.getQueue().addQueue(data)) {
                queueList.add(data);
                BetterRTP.debug("Queue position added " + data.getLocation().toString());
            } else
                BetterRTP.debug("Database error occured for a queue! " + data.getLocation().toString());
        } else if (data.getLocation() != null) {
            /*BetterRTP.debug("Queue position wasn't safe " + data.getLocation().toString());
            if (BetterRTP.getInstance().getSettings().isDebug()) {
                Logger log = BetterRTP.getInstance().getLogger();
                log.info("- CenterX: " + rtpWorld.getCenterX());
                log.info("- CenterZ: " + rtpWorld.getCenterZ());
                log.info("- MaxRadius: " + rtpWorld.getMaxRadius());
                log.info("- MinRadius: " + rtpWorld.getMinRadius());
                log.info("- MinY: " + rtpWorld.getMinY());
                log.info("- MaxY: " + rtpWorld.getMaxY());
            }*/
        } else
            BetterRTP.debug("Queue position wasn't able to generate a location!");
    }

    public static List<QueueData> getApplicable(RTPWorld rtpWorld) {
        List<QueueData> queueData = BetterRTP.getInstance().getQueue().queueList;
        List<QueueData> available = new ArrayList<>();
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
        return available;
    }

    public static List<QueueData> getApplicable(World world) {
        List<QueueData> available = new ArrayList<>();
        List<QueueData> queueData = BetterRTP.getInstance().getQueue().queueList;
        for (QueueData queue : queueData)
            if (Objects.equals(queue.location.getWorld().getName(), world.getName()))
                available.add(queue);
        return available;
    }

    public static int getCount() {
        return BetterRTP.getInstance().getQueue().queueList.size();
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

