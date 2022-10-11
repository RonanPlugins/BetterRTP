package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdLocation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_ERROR_REQUEST_REASON;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HelperRTP {

    //Teleporter and sender are the same
    public static void tp(Player player, World world, List<String> biomes, RTP_TYPE rtpType) {
        tp(player, player, world, biomes, rtpType, false, false);
    }

    //Teleported and Sender MAY be different
    public static void tp(Player player, CommandSender sendi, World world, List<String> biomes, RTP_TYPE rtpType) {
        tp(player, sendi, world, biomes, rtpType, false, false);
    }

    //
    public static void tp(Player player, CommandSender sendi, World world, List<String> biomes, RTP_TYPE rtpType,
                   boolean ignoreCooldown, boolean ignoreDelay) {
        tp(player, sendi, world, biomes, rtpType, ignoreCooldown, ignoreDelay, null);
    }

    public static void tp(@NotNull Player player, CommandSender sendi, @Nullable World world, List<String> biomes, RTP_TYPE rtpType,
                          boolean ignoreCooldown, boolean ignoreDelay, WorldLocations locations) {
        if (world == null)
            world = player.getWorld();
        if (BetterRTP.getInstance().getRTP().overriden.containsKey(world.getName()))
            world = Bukkit.getWorld(BetterRTP.getInstance().getRTP().overriden.get(world.getName()));
        RTP_ERROR_REQUEST_REASON cantReason = HelperRTP_Check.canRTP(player, sendi, world, ignoreCooldown);
        // Not forced and has 'betterrtp.world.<world>'
        if (cantReason != null) {

        }
        boolean delay = false;
        if (!ignoreDelay && sendi == player) //Forced?
            if (getPl().getSettings().isDelayEnabled() && getPl().getSettings().getDelayTime() > 0) //Delay enabled?
                if (!PermissionNode.BYPASS_DELAY.check(player)) //Can bypass?
                    delay = true;
        RTPSetupInformation setup_info = new RTPSetupInformation(world, sendi, player, true,
                biomes, delay, rtpType, locations, !ignoreCooldown && HelperRTP_Check.checkCooldown(sendi, player)); //ignore cooldown or else
        getPl().getRTP().start(setup_info);
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }

    @Nullable
    public static WorldLocations getRandomLocation(CommandSender sender, World world) {
        HashMap<String, RTPWorld> locations_permissible = CmdLocation.getLocations(sender, world);
        if (!locations_permissible.isEmpty()) {
            List<String> valuesList = new ArrayList<>(locations_permissible.keySet());
            String randomIndex = valuesList.get(new Random().nextInt(valuesList.size()));
            return (WorldLocations) locations_permissible.get(randomIndex);
        }
        return null;
    }


}
