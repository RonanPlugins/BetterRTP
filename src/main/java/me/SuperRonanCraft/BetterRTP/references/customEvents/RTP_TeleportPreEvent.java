package me.SuperRonanCraft.BetterRTP.references.customEvents;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class RTP_TeleportPreEvent extends RTPEvent implements Cancellable { //Called upon every rtp call, does not mean player will be teleported

    @Getter Player p;
    boolean cancelled;

    public RTP_TeleportPreEvent(Player p) {
        this.p = p;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
