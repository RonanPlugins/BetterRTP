package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.worlds.WorldLocations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RTPSetupInformation {
    //Will provide information to setup an RTP attempt
    public String world;
    public CommandSender sender;
    public Player player;
    public boolean personalized;
    public List<String> biomes;
    public WorldLocations location;
    public boolean delay;
    public RTP_TYPE rtp_type;

    public RTPSetupInformation(String world, CommandSender sender, Player player, boolean personalized) {
        this.world = world;
        this.sender = sender;
        this.player = player;
        this.personalized = personalized;
    }

    public RTPSetupInformation(String world, CommandSender sender, Player player, boolean personalized, List<String> biomes) {
        this.world = world;
        this.sender = sender;
        this.player = player;
        this.personalized = personalized;
        this.biomes = biomes;
    }

    public RTPSetupInformation(String world, CommandSender sender, Player player, boolean personalized, List<String> biomes,
                               boolean delay, RTP_TYPE rtp_type, WorldLocations location) {
        this.world = world;
        this.sender = sender;
        this.player = player;
        this.personalized = personalized;
        this.biomes = biomes;
        this.delay = delay;
        this.rtp_type = rtp_type;
        this.location = location;
    }
}
