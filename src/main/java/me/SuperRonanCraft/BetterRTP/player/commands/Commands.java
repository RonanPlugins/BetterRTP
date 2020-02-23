package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Commands {

    private Main pl;
    public HashMap<UUID, Long> cooldowns = new HashMap<>();
    public HashMap<UUID, Boolean> rtping = new HashMap<>();
    private boolean cooldownTimer;
    private int timer, cooldown;
    public static String[] cmds = {"help", "player", "world", "version", "reload", "biome"};

    public Commands(Main pl) {
        this.pl = pl;
    }

    public void load() {
        FileBasics.FILETYPE config = pl.getFiles().getType(FileBasics.FILETYPE.CONFIG);
        timer = config.getInt("Settings.Delay.Time");
        cooldownTimer = config.getBoolean("Settings.Cooldown.Enabled");
        cooldown = config.getInt("Settings.Cooldown.Time");
        cooldowns.clear();
    }

    public void commandExecuted(CommandSender sendi, String label, String[] args) {
        if (pl.getPerms().getUse(sendi)) {
            if (args.length > 0) {
                for (CommandTypes cmd : CommandTypes.values()) {
                    if (cmd.name().equalsIgnoreCase(args[0])) {
                        if (cmd.getCmd().permission(sendi))
                            cmd.getCmd().execute(sendi, label, args);
                        else
                            noPerm(sendi);
                        return;
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
                if (cmd.name().toLowerCase().startsWith(args[0].toLowerCase()) && cmd.getCmd().permission(sendi))
                    list.add(cmd.name().toLowerCase());
            }
        } else if (args.length > 1) {
            for (CommandTypes cmd : CommandTypes.values()) {
                if (cmd.name().equalsIgnoreCase(args[0]) && cmd.getCmd().permission(sendi)) {
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
        if (cooldown(sendi, player)) {
            boolean delay = false;
            if (!pl.getPerms().getBypassDelay(player))
                if (timer != 0)
                    if (sendi == player)
                        delay = true;
            pl.getRTP().start(player, sendi, world, biomes, delay);
        }
    }

    private boolean cooldown(CommandSender sendi, Player player) {
        if (sendi != player || pl.getPerms().getBypassCooldown(player))
            return true;
        else if (rtping.containsKey(player.getUniqueId()))
            if (rtping.get(player.getUniqueId())) {
                pl.getText().getAlready(player);
                return false;
            }
        if (cooldownTimer) {
            Player p = (Player) sendi;
            if (cooldowns.containsKey(p.getUniqueId())) {
                long Left = ((cooldowns.get(p.getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);
                if (!pl.getPerms().getBypassDelay(p))
                    Left = Left + timer;
                if (Left > 0) {
                    // Still cooling down
                    pl.getText().getCooldown(sendi, String.valueOf(Left));
                    return false;
                } else {
                    cooldowns.remove(p.getUniqueId());
                    return true;
                }
            } else
                cooldowns.put(p.getUniqueId(), System.currentTimeMillis());
        }
        return true;
    }
}
