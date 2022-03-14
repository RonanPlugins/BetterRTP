package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RTPSetupInformation {
    //Will provide information to setup an RTP attempt
    @Getter @Setter private String world;
    @Getter private final CommandSender sender;
    @Getter private final Player player;
    @Getter private final boolean personalized;
    @Getter private final boolean cooldown;
    @Getter @Setter private List<String> biomes;
    @Getter @Setter private WorldLocations location;
    @Getter private final boolean delay;
    @Getter private final RTP_TYPE rtp_type;

    public RTPSetupInformation(String world, CommandSender sender, Player player, boolean personalized) {
        this(world, sender, player, personalized, null, false, null, null);
    }

    public RTPSetupInformation(String world, CommandSender sender, Player player, boolean personalized, List<String> biomes,
                                boolean delay, RTP_TYPE rtp_type, WorldLocations location) {
        this(world, sender, player, personalized, biomes, delay, rtp_type, location, true);
    }

    public RTPSetupInformation(String world, CommandSender sender, Player player, boolean personalized, List<String> biomes,
                               boolean delay, RTP_TYPE rtp_type, WorldLocations location, boolean cooldown) {
        this.world = world;
        this.sender = sender;
        this.player = player;
        this.personalized = personalized;
        this.biomes = biomes;
        this.delay = delay;
        this.rtp_type = rtp_type;
        this.location = location;
        if (this.world == null) {
            if (player != null)
                this.world = player.getWorld().getName();
            else if (this.location != null)
                this.world = this.location.getWorld().getName();
        }
        this.cooldown = cooldown;
    }
}
