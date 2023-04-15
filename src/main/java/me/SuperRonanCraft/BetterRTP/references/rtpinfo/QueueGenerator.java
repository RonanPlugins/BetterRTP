package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTP_SETUP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPlayer;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldCustom;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueGenerator {


    boolean loaded = false;
    public static final int queueMax = 32, queueMin = 2; //Amount to ready up for each rtp world
    private final int queueMaxAttempts = 50;
    boolean generating;
    private BukkitTask task;

    public void unload() {
        if (task != null)
            Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

    public void load() {
        loaded = false;
        if (task != null)
            Bukkit.getScheduler().cancelTask(task.getTaskId());
        generate(null);
    }

    void generate(@Nullable RTPWorld rtpWorld) {
        if (!QueueHandler.isEnabled()) return;
        Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {
            if (!DatabaseHandler.getQueue().isLoaded()) {
                generate(rtpWorld);
                return;
            }
            loaded = true;
            //BetterRTP.debug("Loaded " + queueList.size() + " previously generated safe locations!");
            //Queue after everything was loaded
            BetterRTP.debug("Attempting to queue up some more safe locations...");
            queueGenerator(new ReQueueData(rtpWorld, queueMax, queueMin, 0, "noone", 0));
        }, 10L);
    }

    private void queueGenerator(ReQueueData data) {
        generating = true;
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {
            //BetterRTP.debug("Generating a new position... attempt # " + data.attempts);
            //Generate more locations
            //Rare cases where a rtp world didn't have a location generated (Permission Groups?)
            if (data.rtpWorld != null) {
                List<QueueData> applicable = QueueHandler.getApplicableAsync(data.rtpWorld);
                String type = "rtp_" + (data.rtpWorld.getID() != null ? data.rtpWorld.getID() : data.rtpWorld.getWorld().getName());
                int newCount = data.lastType.equalsIgnoreCase(type) ? data.lastCount : applicable.size();
                int attempt = data.lastType.equalsIgnoreCase(type) ? data.attempts + 1: 0;
                if (newCount < queueMin && applicable.size() < queueMax) {
                    if (attempt > queueMaxAttempts) {
                        BetterRTP.debug("Max attempts to create a Queue reached for " + type + " (amount: " + applicable.size() + ")");
                        return;
                    }

                    addQueue(data.rtpWorld, type, new ReQueueData(data.rtpWorld, queueMax, queueMin, newCount, type, attempt)); //Generate another later

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
                    List<QueueData> applicable = QueueHandler.getApplicableAsync(world);
                    int newCount = data.lastType.equalsIgnoreCase(type) ? data.lastCount : applicable.size();
                    int attempt = data.lastType.equalsIgnoreCase(type) ? data.attempts + 1 : 0;
                    if (newCount < queueMin && applicable.size() < queueMax) {
                        if (attempt > queueMaxAttempts) {
                            BetterRTP.debug("Max attempts to create a Queue reached for " + type + " (amount: " + applicable.size() + ")");
                            continue;
                        }
                        //Generate a location sync to bukkit api
                        addQueue(world, type, new ReQueueData(null, queueMax, queueMin, newCount, type, attempt)); //Generate another when done later

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

    private void addQueue(RTPWorld rtpWorld, String id, ReQueueData reQueueData) {
        Location loc = RandomLocation.generateLocation(rtpWorld);
        if (loc != null) {
            Bukkit.getScheduler().runTask(BetterRTP.getInstance(), () -> {
                //BetterRTP.debug("Queued up a new position, attempts " + reQueueData.attempts);
                PaperLib.getChunkAtAsync(loc)
                        .thenAccept(v -> {
                            Location safeLoc = RandomLocation.getSafeLocation(
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
                                        //queueList.add(data);
                                        String _x = String.valueOf(data.getLocation().getBlockX());
                                        String _y = String.valueOf(data.getLocation().getBlockY());
                                        String _z = String.valueOf(data.getLocation().getBlockZ());
                                        String _world = data.getLocation().getWorld().getName();
                                        BetterRTP.debug("Queue position generated"
                                                + ": id= " + id + ", database_ID= " + data.database_id
                                                + ", location= x: " + _x + ", y: " + _y + ", z: " + _z + ", world: " + _world);
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
}
