package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import com.tcoded.folialib.wrapper.task.WrappedTask;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTP_SETUP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldCustom;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;

public class QueueGenerator {


    boolean loaded = false;
    public static final int queueMax = 32, queueMin = 2; //Amount to ready up for each rtp world
    private final int queueMaxAttempts = 50;
    boolean generating;
    private WrappedTask task;

    public void unload() {
        if (task != null)
            task.cancel();
    }

    public void load() {
        unload();
        loaded = false;
        generate(null);
    }

    void generate(@Nullable RTPWorld rtpWorld) {
        if (!QueueHandler.isEnabled()) return;
        AsyncHandler.asyncLater(() -> {
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
        task = AsyncHandler.asyncLater(() -> {
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
        }, 20L/*delay before starting queue generator*/);
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
            this.at
