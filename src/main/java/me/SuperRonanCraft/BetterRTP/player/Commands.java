package me.SuperRonanCraft.BetterRTP.player;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Commands {

    private Main pl;
    HashMap<UUID, Long> cooldowns = new HashMap<>();
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

    public void commandExecuted(CommandSender sendi, String cmd, String[] args) {
        if (pl.getPerms().getUse(sendi))
            if (args == null)
                rtp(sendi, cmd, null, null);
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase(cmds[0]))
                    help(sendi, cmd);
                else if (args[0].equalsIgnoreCase(cmds[1]))
                    player(sendi, cmd, args);
                else if (args[0].equalsIgnoreCase(cmds[2]))
                    world(sendi, cmd, args);
                else if (args[0].equalsIgnoreCase(cmds[3]))
                    version(sendi);
                else if (args[0].equalsIgnoreCase(cmds[4]))
                    reload(sendi);
                else if (args[0].equalsIgnoreCase(cmds[5]))
                    biome(sendi, cmd, args);
                else
                    invalid(sendi, cmd);
            } else if (args.length >= 2 && args.length <= 3) {
                if (args[0].equalsIgnoreCase(cmds[1]))
                    player(sendi, cmd, args);
                else if (args[0].equalsIgnoreCase(cmds[2]))
                    world(sendi, cmd, args);
                else if (args[0].equalsIgnoreCase(cmds[5]))
                    biome(sendi, cmd, args);
                else
                    invalid(sendi, cmd);
            } else if (args.length > 3) {
                if (args[0].equalsIgnoreCase(cmds[5]))
                    biome(sendi, cmd, args);
                else if (args[0].equalsIgnoreCase(cmds[1]))
                    player(sendi, cmd, args);
                else
                    invalid(sendi, cmd);
            } else
                rtp(sendi, cmd, null, null);
        else
            noPerm(sendi);
    }

    public List<String> onTabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (String s : cmds)
                if (s.startsWith(args[0]) && permOf(sendi, s))
                    list.add(s);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase(cmds[1]) && permOf(sendi, cmds[1])) {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (p.getDisplayName().startsWith(args[1]))
                        list.add(p.getDisplayName());
            } else if (args[0].equalsIgnoreCase(cmds[2]) && permOf(sendi, cmds[2]))
                for (World w : Bukkit.getWorlds())
                    if (w.getName().startsWith(args[1]) && !pl.getRTP().disabledWorlds().contains(w.getName()) && pl
                            .getPerms().getAWorld(sendi, w.getName()))
                        list.add(w.getName());
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase(cmds[1]) && permOf(sendi, cmds[1])) {
                for (World w : Bukkit.getWorlds())
                    if (w.getName().startsWith(args[2]))
                        list.add(w.getName());
            } else if (args[0].equalsIgnoreCase(cmds[2]) && permOf(sendi, cmds[2]) && permOf(sendi, cmds[5]))
                addBiomes(list, args);
        } else if (args.length > 3) {
            if (args[0].equalsIgnoreCase(cmds[2]) && permOf(sendi, cmds[2]) && permOf(sendi, cmds[5])) {
                addBiomes(list, args);
            } else if (args[0].equalsIgnoreCase(cmds[1]) && permOf(sendi, cmds[1]) && permOf(sendi, cmds[5]))
                addBiomes(list, args);
        }
        if (args[0].equalsIgnoreCase(cmds[5]) && permOf(sendi, cmds[5]))
            addBiomes(list, args);
        return list;
    }

    private void addBiomes(List<String> list, String[] args) {
        try {
            for (Biome b : Biome.values())
                if (b.name().toUpperCase().replaceAll("minecraft:", "").startsWith(args[args.length - 1].toUpperCase()))
                    list.add(b.name().replaceAll("minecraft:", ""));
        } catch (NoSuchMethodError e) {
            //Not in 1.14.X
        }
    }

    //COMMANDS
    private void rtp(CommandSender sendi, String cmd, String world, List<String> biomes) {
        if (sendi instanceof Player)
            tp((Player) sendi, sendi, world, biomes);
        else
            sendi.sendMessage(pl.getText().colorPre("Must be a player to use this command! Try '/" + cmd + " help'"));
    }

    private void help(CommandSender sendi, String cmd) {
        pl.getText().getHelpList(sendi, cmd);
        if (pl.getPerms().getRtpOther(sendi))
            pl.getText().getHelpPlayer(sendi, cmd);
        if (sendi instanceof Player) {
            if (pl.getPerms().getAWorld(sendi, null))
                pl.getText().getHelpWorld(sendi, cmd);
        } else
            pl.getText().getHelpWorld(sendi, cmd);
        if (pl.getPerms().getReload(sendi))
            pl.getText().getHelpReload(sendi, cmd);
    }

    @SuppressWarnings("all")
    private void player(CommandSender sendi, String cmd, String[] args) {
        if (permOf(sendi, args[0]))
            if (args.length == 2)
                if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline())
                    tp(Bukkit.getPlayer(args[1]), sendi, Bukkit.getPlayer(args[1]).getWorld().getName(), null);
                else if (Bukkit.getPlayer(args[1]) != null)
                    playerNotOnline(sendi, args[1]);
                else
                    usage(sendi, cmd, args[0]);
            else if (args.length >= 3)
                if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline())
                    tp(Bukkit.getPlayer(args[1]), sendi, Bukkit.getWorld(args[2]).getName(), getBiomes(args, 3, sendi));
                else if (Bukkit.getPlayer(args[1]) != null)
                    playerNotOnline(sendi, args[1]);
                else
                    usage(sendi, cmd, args[0]);
            else
                usage(sendi, cmd, args[0]);
        else
            noPerm(sendi);
    }

    //rtp world <world> <biome1, biome2...>
    private void world(CommandSender sendi, String cmd, String[] args) {
        if (permOf(sendi, args[0]))
            if (args.length >= 2)
                rtp(sendi, cmd, args[1], getBiomes(args, 2, sendi));
            else
                usage(sendi, cmd, args[0]);
        else
            noPerm(sendi);
    }

    //rtp biome <biome1, biome2...>
    private void biome(CommandSender sendi, String cmd, String[] args) {
        if (permOf(sendi, args[0]))
            if (args.length >= 2)
                rtp(sendi, cmd, null, getBiomes(args, 1, sendi));
            else
                usage(sendi, cmd, args[0]);
        else
            noPerm(sendi);
    }

    private List<String> getBiomes(String[] args, int start, CommandSender sendi) {
        List<String> biomes = new ArrayList<>();
        boolean error_sent = false;
        if (permOf(sendi, cmds[5]))
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

    private void reload(CommandSender sendi) {
        if (pl.getPerms().getReload(sendi))
            pl.reload(sendi);
        else
            noPerm(sendi);
    }

    private void version(CommandSender sendi) {
        sendi.sendMessage(pl.getText().colorPre("&aVersion #&e" + pl.getDescription().getVersion()));
    }

    private void invalid(CommandSender sendi, String cmd) {
        pl.getText().getInvalid(sendi, cmd);
    }

    //INFORMATION
    private void usage(CommandSender sendi, String cmd, String arg) {
        if (arg.equalsIgnoreCase(cmds[1]))
            pl.getText().getUsageRTPOther(sendi, cmd);
        else if (arg.equalsIgnoreCase(cmds[2]))
            pl.getText().getUsageWorld(sendi, cmd);
        else if (arg.equalsIgnoreCase(cmds[5]))
            pl.getText().getUsageBiome(sendi, cmd);
        else
            pl.getText().sms(sendi, "&cSomething went wrong!");
    }

    private void playerNotOnline(CommandSender sendi, String player) {
        pl.getText().getNotOnline(sendi, player);
    }

    private void noPerm(CommandSender sendi) {
        pl.getText().getNoPermission(sendi);
    }

    private void tp(Player player, CommandSender sendi, String world, List<String> biomes) {
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

    private boolean permOf(CommandSender sendi, String cmd) {
        if (cmd.equalsIgnoreCase(cmds[4]))
            return pl.getPerms().getReload(sendi);
        else if (cmd.equalsIgnoreCase(cmds[1]))
            return pl.getPerms().getRtpOther(sendi);
        else if (cmd.equalsIgnoreCase(cmds[2]))
            return pl.getPerms().getWorld(sendi);
        else if (cmd.equalsIgnoreCase(cmds[5]))
            return pl.getPerms().getBiome(sendi);
        return true;
    }
}
