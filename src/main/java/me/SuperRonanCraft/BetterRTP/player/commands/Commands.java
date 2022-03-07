package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownHandler;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.events.RTP_CommandEvent;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldLocations;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    private final BetterRTP pl;
    private int delayTimer;
    public List<RTPCommand> commands = new ArrayList<>();

    public Commands(BetterRTP pl) {
        this.pl = pl;
    }

    public void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        delayTimer = config.getInt("Settings.Delay.Time");
        commands.clear();
        for (RTPCommandType cmd : RTPCommandType.values())
           registerCommand(cmd.getCmd(), false);
    }

    public void registerCommand(RTPCommand cmd, boolean forced) {
        if (!cmd.isDebugOnly() || pl.getSettings().debug || forced) //If debug only, can it be enabled?
            commands.add(cmd);
    }

    public void commandExecuted(CommandSender sendi, String label, String[] args) {
        if (pl.getPerms().getUse(sendi)) {
            if (args != null && args.length > 0) {
                for (RTPCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(args[0])) {
                        if (cmd.permission(sendi)) {
                            cmd.execute(sendi, label, args);
                            //Command Event
                            Bukkit.getServer().getPluginManager().callEvent(new RTP_CommandEvent(sendi, cmd));
                        } else
                            pl.getText().getNoPermission(sendi);
                        return;
                    }
                }
                pl.getText().getInvalid(sendi, label);
            } else
                rtp(sendi, label, null, null);
        } else
            pl.getText().getNoPermission(sendi);
    }

    public List<String> onTabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (RTPCommand cmd : commands) {
                if (cmd.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    if (cmd.permission(sendi))
                        list.add(cmd.getName().toLowerCase());
            }
        } else if (args.length > 1) {
            for (RTPCommand cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(args[0]))
                    if (cmd.permission(sendi)) {
                        List<String> _cmdlist = cmd.tabComplete(sendi, args);
                        if (_cmdlist != null)
                            list.addAll(_cmdlist);
                    }
            }
        }
        return list;
    }

    public void addBiomes(List<String> list, String[] args) {
        try {
            for (Biome b : Biome.values())
                if (b.name().toUpperCase().replaceAll("minecraft:", "").startsWith(args[args.length - 1].toUpperCase()))
                    list.add(b.name().replaceAll("minecraft:", ""));
        } catch (NoSuchMethodError e) {
            //Not in 1.14.X
        }
    }

    public void rtp(CommandSender sendi, String cmd, String world, List<String> biomes) {
        if (sendi instanceof Player)
            tp((Player) sendi, sendi, world, biomes, RTP_TYPE.COMMAND);
        else
            msgNotPlayer(sendi, cmd);
    }

    public void msgNotPlayer(CommandSender sendi, String cmd) {
        sendi.sendMessage(pl.getText().colorPre("Must be a player to use this command! Try '/" + cmd + " help'"));
    }

    //Custom biomes
    public List<String> getBiomes(String[] args, int start, CommandSender sendi) {
        List<String> biomes = new ArrayList<>();
        boolean error_sent = false;
        if (BetterRTP.getInstance().getPerms().getBiome(sendi))
            for (int i = start; i < args.length; i++) {
                String str = args[i];
                try {
                    biomes.add(Biome.valueOf(str.replaceAll(",", "").toUpperCase()).name());
                } catch (Exception e) {
                    if (!error_sent) {
                        pl.getText().getOtherBiome(sendi, str);
                        error_sent = true;
                    }
                }
            }
        return biomes;
    }

    public void tp(Player player, CommandSender sendi, String world, List<String> biomes, RTP_TYPE rtpType) {
        this.tp(player, sendi, world, biomes, rtpType, false, false);
    }

    public void tp(Player player, CommandSender sendi, String world, List<String> biomes, RTP_TYPE rtpType,
                   boolean ignoreCooldown, boolean ignoreDelay) {
        this.tp(player, sendi, world, biomes, rtpType, ignoreCooldown, ignoreDelay, null);
    }

    public void tp(Player player, CommandSender sendi, String world, List<String> biomes, RTP_TYPE rtpType,
                   boolean ignoreCooldown, boolean ignoreDelay, WorldLocations locations) {
        if (checkRTPing(player, sendi)) { //Is RTP'ing
            if (ignoreCooldown || checkCooldown(sendi, player)) { //Is Cooling down
                boolean delay = false;
                if (!ignoreDelay && sendi == player) //Forced?
                    if (pl.getSettings().delayEnabled && delayTimer > 0) //Delay enabled?
                        if (!pl.getPerms().getBypassDelay(player)) //Can bypass?
                            delay = true;
                //player.sendMessage("Cooldown applies: " + cooldownApplies(sendi, player));
                RTPSetupInformation setup_info = new RTPSetupInformation(world, sendi, player, true,
                        biomes, delay, rtpType, locations, !ignoreCooldown && cooldownApplies(sendi, player)); //ignore cooldown or else
                pl.getRTP().start(setup_info);
            }
        }
    }

    private boolean checkRTPing(Player player, CommandSender sendi) {
        if (getPl().getpInfo().getRtping().containsKey(player) && getPl().getpInfo().getRtping().get(player)) {
            pl.getText().getAlready(sendi);
            return false;
        }
        return true;
    }

    private boolean checkCooldown(CommandSender sendi, Player player) {
        if (cooldownApplies(sendi, player)) { //Bypassing/Forced?
            CooldownHandler cooldownHandler = getPl().getCooldowns();
            if (!cooldownHandler.isLoaded() || !cooldownHandler.loadedPlayer(player)) { //Cooldowns have yet to download
                pl.getText().getCooldown(sendi, String.valueOf(-1L));
                return false;
            }
            //Cooldown Data
            CooldownData cooldownData = getPl().getCooldowns().getPlayer(player);
            if (cooldownData != null) {
                if (cooldownHandler.locked(cooldownData)) { //Infinite cooldown (locked)
                    pl.getText().getNoPermission(sendi);
                    return false;
                } else { //Normal cooldown
                    long Left = cooldownHandler.timeLeft(cooldownData);
                    if (pl.getSettings().delayEnabled && !pl.getPerms().getBypassDelay(sendi))
                        Left = Left + delayTimer;
                    if (Left > 0) {
                        //Still cooling down
                        pl.getText().getCooldown(sendi, String.valueOf(Left));
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

    private boolean cooldownOverride(CommandSender sendi, Player player) {
        return sendi != player || pl.getPerms().getBypassCooldown(player);
    }

    private boolean cooldownEnabled() {
        return getPl().getCooldowns().isEnabled();
    }

    private boolean cooldownApplies(CommandSender sendi, Player player) {
        return cooldownEnabled() && !cooldownOverride(sendi, player);
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
