package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTP_SETUP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPlayer;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldCustom;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
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

    public void unload() {
        if (task != null)
            Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

    public void load() {
        loaded = false;
        if (task != null)
            Bukkit.getScheduler().cancelTask(task.getTaskId());

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
            //long usedmemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            //BetterRTP.debug("Memory used " + usedmemory);
            queueList = DatabaseHandler.getQueue().getQueues();
            //usedmemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) - usedmemory;
            //BetterRTP.debug("Memory used after load " + usedmemory);
            loaded = true;
            BetterRTP.debug("Loaded " + queueList.size() + " previously generated safe locations!");
            //Queue after everything was loaded
            BetterRTP.debug("Attempting to queue up some more safe locations...");
            queueGenerator(new ReQueueData(null, queueMax, queueMin, 0, "noone", 0));
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
            queueGenerator(new ReQueueData(e.getWorld(), queueMax, queueMin, 0, "noone", 0));
    }

    @EventHandler
    public void onRTP(RTP_TeleportPostEvent e) {
        //Delete previously used location
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            Location location = e.getLocation();
            QueueHandler.remove(location);
        });
    }

    private void queueGenerator(ReQueueData data) {
        generating = true;
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {
            //BetterRTP.debug("Generating a new position... attemp # " + data.attempts);
            //Generate more locations
            //Rare cases where a rtp world didnt have a location generated (Permission Groups?)
            if (data.rtpWorld != null) {
                List<QueueData> applicable = getApplicable(data.rtpWorld);
                String type = "rtp_" + (data.rtpWorld.getID() != null ? data.rtpWorld.getID() : data.rtpWorld.getWorld().getName());
                int newCount = data.lastType.equalsIgnoreCase(type) ? data.lastCount : applicable.size();
                int attempt = data.lastType.equalsIgnoreCase(type) ? data.attempts + 1: 0;
                if (newCount < queueMin && applicable.size() < queueMax) {
                    if (attempt > queueMaxAttempts) {
                        BetterRTP.debug("Max attempts to create a Queue reached for " + type + " (amount: " + applicable.size() + ")");
                        return;
                    }

                    generateFromWorld(data.rtpWorld, type, new ReQueueData(data.rtpWorld, queueMax, queueMin, newCount, type, attempt)); //Generate another later

                    return;
                }
                if (data.lastType.equalsIgnoreCase(type))
                    BetterRTP.debug("Queue max reached for " + type + " (amount: " + applicable.size() + ") lastCount: " + data.lastCount);
            }

            //Queue up all setup types
            for (RTP_SETUP_TYPE setup : RTP_SETUP_TYPE.values()) {
                HashMap<String, RTPWorld> map = getFromSetup(setup);
                if (map == null) continue;
                for (Map.Entry<String, RTPWorld> rtpWorldEntry : map.entrySet()) {
                    RTPWorld world = rtpWorldEntry.getValue();
                    String type = getId(setup, rtpWorldEntry.getKey());
                    List<QueueData> applicable = getApplicable(world);
                    int newCount = data.lastType.equalsIgnoreCase(type) ? data.lastCount : applicable.size();
                    int attempt = data.lastType.equalsIgnoreCase(type) ? data.attempts + 1 : 0;
                    if (newCount < queueMin && applicable.size() < queueMax) {
                        if (attempt > queueMaxAttempts) {
                            BetterRTP.debug("Max attempts to create a Queue reached for " + type + " (amount: " + applicable.size() + ")");
                            continue;
                        }
                        //Generate a location sync to bukkit api
                        generateFromWorld(world, type, new ReQueueData(null, queueMax, queueMin, newCount, type, attempt)); //Generate another when done later

                        return;
                    }
                    if (data.lastType.equalsIgnoreCase(type))
                        BetterRTP.debug("Max queue reached for " + type + " (amount: " + applicable.size() + ") lastCount: " + data.lastCount);
                }
            }
            generating = false;
            BetterRTP.debug("Queueing paused, max queue limit reached!");
        }, 20L /*delay before starting queue generator*/);
    }

    static class ReQueueData {

        RTPWorld rtpWorld;
        int queueMax, queueMin, lastCount, attempts;
        String lastType;
        ReQueueData(RTPWorld rtpWorld, int queueMax, int queueMin, int lastCount, String lastType, int attempts) {
            this.rtpWorld = rtpWorld;
            this.queueMax = queueMax;
            this.queueMin = queueMin;
            this.lastCount = lastCount;
            this.lastType = lastType;
            this.attempts = attempts;
        }
    }

    private static HashMap<String, RTPWorld> getFromSetup(RTP_SETUP_TYPE type) {
        switch (type) {
            case LOCATION: return BetterRTP.getInstance().getRTP().getRTPworldLocations();
            case CUSTOM_WORLD: return BetterRTP.getInstance().getRTP().getRTPcustomWorld();
            case DEFAULT:
                HashMap<String, RTPWorld> list = new HashMap<>();
                RTP rtp = BetterRTP.getInstance().getRTP();
                for (World world : Bukkit.getWorlds())
                    if (!rtp.getDisabledWorlds().contains(world.getName()) && !rtp.getRTPcustomWorld().containsKey(world.getName()))
                        list.put(world.getName(), new WorldCustom(world, rtp.getRTPdefaultWorld()));
                return list;
        }
        return null;
    }

    private static String getId(RTP_SETUP_TYPE type, String id) {
        switch (type) {
            case CUSTOM_WORLD: return "custom_" + id;
            case LOCATION: return "location_" + id;
            case DEFAULT: return "default_" + id;
        }
        return "unknown_" + id;
    }

    //Generate a safe location
    private void generateFromWorld(RTPWorld world, String id, ReQueueData reQueueData) {
        addQueue(world, id, reQueueData);
    }

    private void addQueue(RTPWorld rtpWorld, String id, ReQueueData reQueueData) {
        Location loc = WorldPlayer.generateLocation(rtpWorld);
        if (loc != null) {
            Bukkit.getScheduler().runTask(BetterRTP.getInstance(), () -> {
                //BetterRTP.debug("Queued up a new position, attempts " + reQueueData.attempts);
                PaperLib.getChunkAtAsync(loc)
                        .thenAccept(v -> {
                            Location safeLoc = RTPPlayer.getSafeLocation(
                                    HelperRTP.getWorldType(rtpWorld.getWorld()),
                                    loc.getWorld(),
                                    loc,
                                    rtpWorld.getMinY(),
                                    rtpWorld.getMaxY(),
                                    rtpWorld.getBiomes());
                            //data.setLocation(safeLoc);
                            if (safeLoc != null) {
                                Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
                                    QueueData data = DatabaseHandler.getQueue().addQueue(safeLoc);
                                    if (data != null) {
                                        queueList.add(data);
                                        String _x = String.valueOf(data.getLocation().getBlockX());
                                        String _y = String.valueOf(data.getLocation().getBlockY());
                                        String _z = String.valueOf(data.getLocation().getBlockZ());
                                        String _world = data.getLocation().getWorld().getName();
                                        BetterRTP.debug("Queue position generated #" + queueList.size()
                                                + ": id= " + id + ", database_ID" + data.database_id
                                                + ", location= x:" + _x + ", y:" + _y + ", z:" + _z + ", world:" + _world);
                                    } else
                                        BetterRTP.debug("Database error occurred for a queue when trying to save: " + safeLoc);
                                    queueGenerator(reQueueData);
                                });
                            } else
                                queueGenerator(reQueueData);
                        });
            });
        } else {
            BetterRTP.debug("Queue position wasn't able to generate a location!");
            queueGenerator(reQueueData);
        }
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

    public static void remove(Location loc) {
        List<QueueData> deleteList = new ArrayList<>();
        for (QueueData data : BetterRTP.getInstance().getQueue().queueList) {
            Location dataLoc = data.getLocation();
            if (loc.getBlockX() == dataLoc.getBlockX()
                    && loc.getBlockZ() == dataLoc.getBlockZ()
                    && loc.getWorld().getName().equals(dataLoc.getWorld().getName())) {
                deleteList.add(data);
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            deleteList.forEach(data -> {
                //Delete all queue data async
                if (DatabaseHandler.getQueue().removeQueue(data)) {
                    BetterRTP.getInstance().getQueue().queueList.remove(data);
                    BetterRTP.debug("-Removed a queue " + data.getLocation().toString());
                }
            });
        });
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
        boolean ztop = z >= center_z - radius && z <= center_z - radius_min;
        return ztop || zbottom;
    }
}

