package me.SuperRonanCraft.BetterRTP.player.events;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RTPEventInitiator {
    private final List<RTPEventListener> listeners = new ArrayList<>();

    public void addListener(RTPEventListener toAdd) {
        listeners.add(toAdd);
    }

    public void eventCall_Teleport(Player p) {
        System.out.println("Hello!!");

        // Notify everybody that may be interested.
        for (RTPEventListener l : listeners)
            l.teleportedEvent(p);
    }
}
