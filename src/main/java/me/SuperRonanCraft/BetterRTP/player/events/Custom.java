package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTP.references.depends.DepEssentials;

public class Custom {

    static void postRTP(RTP_TeleportPostEvent e) {
        DepEssentials.setBackLocation(e.getPlayer(), e.getOldLocation());
    }

}
