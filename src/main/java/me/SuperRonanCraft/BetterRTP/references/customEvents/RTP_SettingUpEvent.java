package me.SuperRonanCraft.BetterRTP.references.customEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class RTP_SettingUpEvent extends RTPEvent implements Cancellable {

    Player p;
    boolean cancelled = false;

    public RTP_SettingUpEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
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
