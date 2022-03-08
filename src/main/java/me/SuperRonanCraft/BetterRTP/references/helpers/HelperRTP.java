package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelperRTP {

    public static void rtp(CommandSender sendi, String cmd, String world, List<String> biomes) {
        if (sendi instanceof Player)
            HelperRTP.tp((Player) sendi, sendi, world, biomes, RTP_TYPE.COMMAND);
        else
            BetterRTP.getInstance().getText().getNotPlayer(sendi, cmd);
    }

    public static void tp(Player player, CommandSender sendi, String world, List<String> biomes, RTP_TYPE rtpType) {
        tp(player, sendi, world, biomes, rtpType, false, false);
    }

    public static void tp(Player player, CommandSender sendi, String world, List<String> biomes, RTP_TYPE rtpType,
                   boolean ignoreCooldown, boolean ignoreDelay) {
        tp(player, sendi, world, biomes, rtpType, ignoreCooldown, ignoreDelay, null);
    }

    public static void tp(Player player, CommandSender sendi, String world, List<String> biomes, RTP_TYPE rtpType,
                   boolean ignoreCooldown, boolean ignoreDelay, WorldLocations locations) {
        if (checkRTPing(player, sendi)) { //Is RTP'ing
            if (ignoreCooldown || checkCooldown(sendi, player)) { //Is Cooling down
                boolean delay = false;
                if (!ignoreDelay && sendi == player) //Forced?
                    if (getPl().getSettings().delayEnabled && getPl().getSettings().delayTime > 0) //Delay enabled?
                        if (!getPl().getPerms().getBypassDelay(player)) //Can bypass?
                            delay = true;
                //player.sendMessage("Cooldown applies: " + cooldownApplies(sendi, player));
                RTPSetupInformation setup_info = new RTPSetupInformation(world, sendi, player, true,
                        biomes, delay, rtpType, locations, !ignoreCooldown && cooldownApplies(sendi, player)); //ignore cooldown or else
                getPl().getRTP().start(setup_info);
            }
        }
    }

    private static boolean checkRTPing(Player player, CommandSender sendi) {
        if (getPl().getpInfo().getRtping().containsKey(player) && getPl().getpInfo().getRtping().get(player)) {
            getPl().getText().getAlready(sendi);
            return false;
        }
        return true;
    }

    private static boolean checkCooldown(CommandSender sendi, Player player) {
        if (cooldownApplies(sendi, player)) { //Bypassing/Forced?
            CooldownHandler cooldownHandler = getPl().getCooldowns();
            if (!cooldownHandler.isLoaded() || !cooldownHandler.loadedPlayer(player)) { //Cooldowns have yet to download
                getPl().getText().getCooldown(sendi, String.valueOf(-1L));
                return false;
            }
            //Cooldown Data
            CooldownData cooldownData = getPl().getCooldowns().getPlayer(player);
            if (cooldownData != null) {
                if (cooldownHandler.locked(cooldownData)) { //Infinite cooldown (locked)
                    getPl().getText().getNoPermission(sendi);
                    return false;
                } else { //Normal cooldown
                    long Left = cooldownHandler.timeLeft(cooldownData);
                    if (getPl().getSettings().delayEnabled && !getPl().getPerms().getBypassDelay(sendi))
                        Left = Left + getPl().getSettings().delayTime;
                    if (Left > 0) {
                        //Still cooling down
                        getPl().getText().getCooldown(sendi, String.valueOf(Left));
                        return false;
                    } else {
                        //Reset timer, but allow them to tp
                        //cooldowns.add(id);
                        return true;
                    }
                }
            } //else
            //cooldowns.add(id);
        }
        return true;
    }

    private static boolean cooldownOverride(CommandSender sendi, Player player) {
        return sendi != player || getPl().getPerms().getBypassCooldown(player);
    }

    private static boolean cooldownEnabled() {
        return getPl().getCooldowns().isEnabled();
    }

    private static boolean cooldownApplies(CommandSender sendi, Player player) {
        return cooldownEnabled() && !cooldownOverride(sendi, player);
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
