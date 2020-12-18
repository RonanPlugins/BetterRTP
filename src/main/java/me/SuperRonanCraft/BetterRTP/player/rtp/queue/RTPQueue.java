package me.SuperRonanCraft.BetterRTP.player.rtp.queue;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import me.SuperRonanCraft.BetterRTP.references.worlds.RTPWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;

public class RTPQueue implements Listener { //Randomly queues up some randomly safe locations

    HashMap<RTPWorld, List<Location>> queue = new HashMap<>();

    public void load() {
        Bukkit.getPluginManager().registerEvents(this, BetterRTP.getInstance());
        //queue();
    }

    @EventHandler
    public void onRtpFindLoc(RTP_FindLocationEvent e) {
        RTPWorld world = e.getWorld();
    }

    private void queue(RTPWorld world) {

    }
}
