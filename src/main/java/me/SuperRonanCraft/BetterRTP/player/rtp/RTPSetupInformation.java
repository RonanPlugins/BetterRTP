package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RTPSetupInformation {
    //Will provide information to setup an RTP attempt
    public String world;
    public CommandSender sender;
    public Player player;
    public boolean personalized, cooldown;
    public List<String> biomes;
    public WorldLocations location;
    public boolean delay;
    public RTP_TYPE rtp_type;

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
