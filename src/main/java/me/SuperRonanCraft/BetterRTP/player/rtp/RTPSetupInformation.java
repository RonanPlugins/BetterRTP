package me.SuperRonanCraft.BetterRTP.player.rtp;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdLocation;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RTPSetupInformation {
    //Will provide information to setup an RTP attempt
    @Getter @Setter private String world;
    @Getter @NonNull private final CommandSender sender;
    @Getter @Nullable private final Player player;
    @Getter private final boolean personalized;
    @Getter private final boolean cooldown;
    @Getter @Setter @Nullable private List<String> biomes;
    @Getter @Setter @Nullable private WorldLocations location;
    @Getter private final boolean delay;
    @Getter @Nullable private final RTP_TYPE rtp_type;

    public RTPSetupInformation(@Nullable String world,
                               @NonNull CommandSender sender,
                               @Nullable Player player,
                               boolean personalized) {
        this(world, sender, player, personalized, null, false, null, null);
    }

    public RTPSetupInformation(@Nullable String world,
                               @NonNull CommandSender sender,
                               @Nullable Player player,
                               boolean personalized,
                               @Nullable List<String> biomes,
                                boolean delay,
                               @Nullable RTP_TYPE rtp_type,
                               @Nullable WorldLocations location) {
        this(world, sender, player, personalized, biomes, delay, rtp_type, location, true);
    }

    public RTPSetupInformation(@Nullable String world,
                               @NonNull CommandSender sender,
                               @Nullable Player player,
                               boolean personalized,
                               @Nullable List<String> biomes,
                               boolean delay,
                               @Nullable RTP_TYPE rtp_type,
                               @Nullable WorldLocations location,
                               boolean cooldown) {
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
