package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class QueueHandler implements Listener { //Randomly queues up some randomly safe locations

    boolean loaded = false;
    List<QueueData> queueList = new ArrayList<>();
    private final int queueSize = 32;

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

    @EventHandler
    public void onRtpFindLoc(RTP_FindLocationEvent e) {
        //RTPWorld world = e.getWorld();
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
        deleteList.forEach(queueData -> queueList.remove(queueData));
    }
}

