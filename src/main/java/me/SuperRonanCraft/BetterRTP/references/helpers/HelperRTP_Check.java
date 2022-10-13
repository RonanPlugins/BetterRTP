package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_ERROR_REQUEST_REASON;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesCore;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelperRTP_Check {

    public static RTP_ERROR_REQUEST_REASON canRTP(Player player, CommandSender sendi, WorldPlayer pWorld, boolean ignoreCooldown) {
        if (isRTPing(player)) { //Is RTP'ing
            return RTP_ERROR_REQUEST_REASON.IS_RTPING;
        }
        // Not forced and has 'betterrtp.world.<world>'
        if (sendi == player && !PermissionNode.getAWorld(sendi, pWorld.getWorld().getName())) {
            return RTP_ERROR_REQUEST_REASON.NO_PERMISSION;
        }
        // Check disabled worlds
        if (getPl().getRTP().getDisabledWorlds().contains(pWorld.getWorld().getName())) {
            return RTP_ERROR_REQUEST_REASON.WORLD_DISABLED;
        }
        if (sendi == player && checkCooldown(sendi, player) && isCoolingDown(sendi, player, pWorld)) { //Is Cooling down
            return RTP_ERROR_REQUEST_REASON.COOLDOWN;
        }
        if (getPl().getEco().hasBalance(sendi, pWorld))
            return RTP_ERROR_REQUEST_REASON.PRICE_ECONOMY;
        if (getPl().getEco().hasHunger(sendi, pWorld))
            return RTP_ERROR_REQUEST_REASON.PRICE_HUNGER;
        return null;
    }

    private static boolean isRTPing(Player player) {
        return getPl().getpInfo().getRtping().getOrDefault(player, false);
    }

    public static boolean isCoolingDown(CommandSender sendi, Player player, WorldPlayer pWorld) {
        if (!checkCooldown(sendi, player))  //Bypassing/Forced?
            return false;
        CooldownHandler cooldownHandler = getPl().getCooldowns();
        if (!cooldownHandler.isLoaded() || !cooldownHandler.loadedPlayer(player)) { //Cooldowns have yet to download
            MessagesCore.COOLDOWN.send(sendi, String.valueOf(-1L));
            return true;
        }
        //Cooldown Data
        CooldownData cooldownData = getPl().getCooldowns().get(player, pWorld.getWorld());
        if (cooldownData != null) {
            if (cooldownData.getTime() == 0) //Global cooldown with nothing
                return false;
            else if (cooldownHandler.locked(player)) { //Infinite cooldown (locked)
                MessagesCore.NOPERMISSION.send(sendi);
                return true;
            } else { //Normal cooldown
                long timeLeft = cooldownHandler.timeLeft(player, cooldownData, pWorld);
                if (timeLeft > 0) {
                    //Still cooling down
                    MessagesCore.COOLDOWN.send(sendi, String.valueOf(timeLeft));
                    return true;
                } else {
                    //Reset timer, but allow them to tp
                    //cooldowns.add(id);
                    return false;
                }
            }
        }
        return false;
    }

    static boolean checkCooldown(CommandSender sendi, Player player) {
        return getPl().getCooldowns().isEnabled() && !(sendi != player || PermissionNode.BYPASS_COOLDOWN.check(player));
    }

    static boolean isDelay(Player player, CommandSender sendi) {
        boolean delay = false;
        if (sendi == player) //Forced?
            if (getPl().getSettings().isDelayEnabled() && getPl().getSettings().getDelayTime() > 0) //Delay enabled?
                if (!PermissionNode.BYPASS_DELAY.check(player)) //Can bypass?
                    delay = true;
        return delay;
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
