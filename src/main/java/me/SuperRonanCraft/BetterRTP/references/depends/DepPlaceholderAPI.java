package me.SuperRonanCraft.BetterRTP.references.depends;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperDate;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Command;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Info;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.references.settings.Settings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
            return cooldown(data, player.getWorld());
        } else if (request.startsWith("cooldown_")) {
            World world = null;
            String world_name = request.replace("cooldown_", "");
            if (world_name.length() > 0) {
                for (World _world : Bukkit.getWorlds()) {
                    if (world_name.equalsIgnoreCase(_world.getName())) {
                        world = _world;
                        break;
                    }
                }
            }
            return cooldown(data, world);
        } else if (request.startsWith("canrtp_")) {
            String world_name = request.replace("canrtp_", "");
            World world = null;
            if (world_name.length() > 0)
                for (World _world : Bukkit.getWorlds())
                    if (world_name.equalsIgnoreCase(_world.getName())) {
                        world = _world;
                        break;
                    }
            return canRTP(player, world);
        } else if (request.equalsIgnoreCase("canrtp")) {
            World world = player.getWorld();
            return canRTP(player, world);
        }
        return null;
    }

    private String cooldown(PlayerData data, World world) {
        if (world == null) return "Invalid World";
        if (BetterRTP.getInstance().getRTP().overriden.containsKey(world.getName()))
            world = Bukkit.getWorld(BetterRTP.getInstance().getRTP().overriden.get(world.getName()));
        CooldownData cooldownData = data.getCooldowns().getOrDefault(world, null);
        if (cooldownData != null)
            return HelperDate.stringFrom(cooldownData.getTime());
        else
            return "None";
    }

    private String canRTP(Player player, World world) {
        if (world == null) return "Invalid World";
        if (BetterRTP.getInstance().getRTP().overriden.containsKey(world.getName()))
            world = Bukkit.getWorld(BetterRTP.getInstance().getRTP().overriden.get(world.getName()));
        //Permission
        if (!PermissionNode.getAWorld(player, world.getName()))
            return BetterRTP.getInstance().getSettings().getPlaceholder_nopermission();
        RTPSetupInformation setupInformation = new RTPSetupInformation(world, player, player, true);
        //Cooldown
        if (!HelperRTP.isCoolingDown(player, player, setupInformation, false))
            return BetterRTP.getInstance().getSettings().getPlaceholder_cooldown();
        WorldPlayer worldPlayer = BetterRTP.getInstance().getRTP().getPlayerWorld(setupInformation);
        //Price
        if (!BetterRTP.getInstance().getEco().hasBalance(player, worldPlayer))
            return BetterRTP.getInstance().getSettings().getPlaceholder_balance();
        //True
        return BetterRTP.getInstance().getSettings().getPlaceholder_true();
    }
}
