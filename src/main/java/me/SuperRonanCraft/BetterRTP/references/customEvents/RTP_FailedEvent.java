package me.SuperRonanCraft.BetterRTP.references.customEvents;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPPlayer;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.Nullable;

//Called when an rtp is finding a valid location
@Getter public class RTP_FailedEvent extends RTPEvent {

    Player p;
    RTPWorld world;
    int attempts;

    public RTP_FailedEvent(RTPPlayer rtpPlayer) {
        this.p = rtpPlayer.getPlayer();
        this.world = rtpPlayer.getWorldPlayer();
        this.attempts = rtpPlayer.getAttempts();
    }
}
