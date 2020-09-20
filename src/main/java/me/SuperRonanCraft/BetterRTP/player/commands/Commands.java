package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.player.rtp.RTPCooldown;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Commands {

    private final Main pl;
    public HashMap<UUID, Boolean> rtping = new HashMap<>();
    public RTPCooldown cooldowns = new RTPCooldown();
    private int delayTimer;

    public Commands(Main pl) {
        this.pl = pl;
    }

    public void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        delayTimer = config.getInt("Settings.Delay.Time");
        cooldowns.load();
        rtping.clear();
    }

    public void commandExecuted(CommandSender sendi, String label, String[] args) {
        if (pl.getPerms().getUse(sendi)) {
            if (args != null && args.length > 0) {
                for (CommandTypes cmd : CommandTypes.values()) {
                    if (cmd.name().equalsIgnoreCase(args[0])) {
                        if (!cmd.isDebugOnly() || pl.getSettings().debug) { //Debug only?
                            if (cmd.getCmd().permission(sendi))
                                cmd.getCmd().execute(sendi, label, args);
                            else
                                noPerm(sendi);
                            return;
                        }
                    }
                }
                invalid(sendi, label);
            } else
                rtp(sendi, label, null, null);
        } else
            noPerm(sendi);
    }

    private void invalid(CommandSender sendi, String cmd) {
        pl.getText().getInvalid(sendi, cmd);
    }

    public List<String> onTabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (CommandTypes cmd : CommandTypes.values()) {
                if (cmd.name().toLowerCase().startsWith(args[0].toLowerCase()))
                    if (!cmd.isDebugOnly() || pl.getSettings().debug) //Debug only?
                        if (cmd.getCmd().permission(sendi))
                            list.add(cmd.name().toLowerCase());
            }
        } else if (args.length > 1) {
            for (CommandTypes cmd : CommandTypes.values()) {
                if (cmd.name().equalsIgnoreCase(args[0]))
                    if (!cmd.isDebugOnly() || pl.getSettings().debug) //Debug only?
                        if (cmd.getCmd().permission(sendi)) {
                            List<String> _cmdlist = cmd.getCmd().tabComplete(sendi, args);
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
            tp((Player) sendi, sendi, world, biomes);
        else
            sendi.sendMessage(pl.getText().colorPre("Must be a player to use this command! Try '/" + cmd + " help'"));
    }

    //Custom biomes
    public List<String> getBiomes(String[] args, int start, CommandSender sendi) {
        List<String> biomes = new ArrayList<>();
        boolean error_sent = false;
        if (Main.getInstance().getPerms().getBiome(sendi))
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

    public void playerNotOnline(CommandSender sendi, String player) {
        pl.getText().getNotOnline(sendi, player);
    }

    private void noPerm(CommandSender sendi) {
        pl.getText().getNoPermission(sendi);
    }

    public void tp(Player player, CommandSender sendi, String world, List<String> biomes) {
        if (checkDelay(sendi, player)) { //Cooling down or rtp'ing
            boolean delay = false;
            if (sendi == player) //Forced?
                if (pl.getSettings().delayEnabled && delayTimer > 0) //Delay enabled?
                    if (!pl.getPerms().getBypassDelay(player)) //Can bypass?
                        delay = true;
            pl.getRTP().start(player, sendi, world, biomes, delay);
        }
    }

    private boolean checkDelay(CommandSender sendi, Player player) {
        if (rtping.containsKey(player.getUniqueId()) && rtping.get(player.getUniqueId())) {
            pl.getText().getAlready(player);
            return false;
        } else if (sendi != player || pl.getPerms().getBypassCooldown(player)) { //Bypassing/Forced?
            return true;
        } else if (cooldowns.enabled) { //Cooling down?
            Player p = (Player) sendi;
            UUID id = p.getUniqueId();
            if (cooldowns.exists(id)) {
                if (cooldowns.locked(id)) { //Infinite cooldown (locked)
                    pl.getText().getNoPermission(sendi);
                    return false;
                } else { //Normal cooldown
                    long Left = cooldowns.timeLeft(id);
                    if (pl.getSettings().delayEnabled && !pl.getPerms().getBypassDelay(p))
                        Left = Left + delayTimer;
                    if (Left > 0) {
                        //Still cooling down
                        pl.getText().getCooldown(sendi, String.valueOf(Left));
                        return false;
                    } else {
                        //Reset timer, but allow them to tp
                        cooldowns.add(id);
                        return true;
                    }
                }
            } else
                cooldowns.add(id);
        }
        return true;
    }
}
