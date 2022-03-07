package me.SuperRonanCraft.BetterRTP.references.events;

import org.bukkit.entity.Player;

public class RTP_TeleportPreEvent extends RTPEvent { //Called upon every rtp call, does not mean player will be teleported

    Player p;

    public RTP_TeleportPreEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

}
