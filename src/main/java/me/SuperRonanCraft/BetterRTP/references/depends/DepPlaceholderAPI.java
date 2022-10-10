package me.SuperRonanCraft.BetterRTP.references.depends;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Command;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Info;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DepPlaceholderAPI extends PlaceholderExpansion {

    @NotNull
    @Override
    public String getIdentifier() {
        return BetterRTP.getInstance().getDescription().getName().toLowerCase();
    }

    @NotNull
    @Override
    public String getAuthor() {
        return BetterRTP.getInstance().getDescription().getAuthors().get(0);
    }

    @NotNull
    @Override
    public String getVersion() {
        return BetterRTP.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String request) {
        PlayerData data = HelperPlayer.getData(player);
        if (request.equalsIgnoreCase("count")) {
            return String.valueOf(data.getRtpCount());
        } else if (request.equalsIgnoreCase("cooldown")) {
            CooldownData cooldownData = data.getCooldowns().getOrDefault(player.getWorld(), null);
            if (cooldownData != null)
                return String.valueOf(cooldownData.getTime());
            else
                return "None";
        } else if (request.startsWith("cooldown_")) {
            CooldownData cooldownData = null;
            String world_name = request.replace("cooldown_", "");
            if (world_name.length() > 0) {
                for (World world : Bukkit.getWorlds()) {
                    if (world_name.equalsIgnoreCase(world.getName())) {
                        cooldownData = data.getCooldowns().getOrDefault(player.getWorld(), null);
                        break;
                    }
                }
            }
            if (cooldownData != null)
                return String.valueOf(cooldownData.getTime());
            else
                return "None";
        } else if (request.startsWith("rtpable_")) {
            String world_name = request.replace("rtpable_", "");
            World world = null;
            if (world_name.length() > 0) {
                for (World _world : Bukkit.getWorlds()) {
                    if (world_name.equalsIgnoreCase(_world.getName())) {
                        world = _world;
                        break;
                    }
                }
            }
            if (world == null) return "Invalid World";
            if (!PermissionNode.getAWorld(player, world.getName()))
                return "No Permission";
            CooldownData cooldownData = HelperPlayer.getData(player).getCooldowns().getOrDefault(world, null);
            //RTPWorld rtpWorld = BetterRTP.getInstance().getRTP().getPlayerWorld(new RTPSetupInformation());
            //if (cooldownData != null && BetterRTP.getInstance().getCooldowns().timeLeft(player, cooldownData, world))
        }
        return null;
    }
}
