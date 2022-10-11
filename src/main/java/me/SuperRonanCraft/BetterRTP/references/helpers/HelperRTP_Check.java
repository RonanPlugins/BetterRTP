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

public class HelperRTP_Check {

    public static RTP_ERROR_REQUEST_REASON canRTP(Player player, CommandSender sendi, World world, boolean ignoreCooldown) {
        if (isRTPing(player)) { //Is RTP'ing
            //getPl().getText().getAlready(sendi);
            return RTP_ERROR_REQUEST_REASON.IS_RTPING;
        }
        // Not forced and has 'betterrtp.world.<world>'
        if (sendi == player && !PermissionNode.getAWorld(sendi, world.getName())) {
            //getPl().getText().getNoPermissionWorld(player, world.getName());
            return RTP_ERROR_REQUEST_REASON.NO_PERMISSION;
        }
        // Check disabled worlds
        if (getPl().getRTP().getDisabledWorlds().contains(world.getName())) {
            //getPl().getText().getDisabledWorld(sendi, world.getName());
            return RTP_ERROR_REQUEST_REASON.WORLD_DISABLED;
        }
        if (sendi == player && checkCooldown(sendi, player) && !isCoolingDown(sendi, player, world)) { //Is Cooling down
            return RTP_ERROR_REQUEST_REASON.COOLDOWN;
        }
        if (getPl().getEco().hasBalance(sendi, ))
            return null;
    }

    private static boolean isRTPing(Player player) {
        return getPl().getpInfo().getRtping().getOrDefault(player, false);
    }

    public static boolean isCoolingDown(CommandSender sendi, Player player, World world) {
        if (!checkCooldown(sendi, player))  //Bypassing/Forced?
            return true;
        CooldownHandler cooldownHandler = getPl().getCooldowns();
        if (!cooldownHandler.isLoaded() || !cooldownHandler.loadedPlayer(player)) { //Cooldowns have yet to download
            getPl().getText().getCooldown(sendi, String.valueOf(-1L));
            return false;
        }
        //Cooldown Data
        CooldownData cooldownData = getPl().getCooldowns().get(player, world);
        if (cooldownData != null) {
            if (cooldownData.getTime() == 0) //Global cooldown with nothing
                return true;
            else if (cooldownHandler.locked(player)) { //Infinite cooldown (locked)
                getPl().getText().getNoPermission(sendi);
                return false;
            } else { //Normal cooldown
                long timeLeft = cooldownHandler.timeLeft(player, cooldownData, world);
                if (timeLeft > 0) {
                    //Still cooling down
                    getPl().getText().getCooldown(sendi, String.valueOf(timeLeft));
                    return false;
                } else {
                    //Reset timer, but allow them to tp
                    //cooldowns.add(id);
                    return true;
                }
            }
        }
        return true;
    }

    private static boolean checkCooldown(CommandSender sendi, Player player) {
        return getPl().getCooldowns().isEnabled() && !(sendi != player || PermissionNode.BYPASS_COOLDOWN.check(player));
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
