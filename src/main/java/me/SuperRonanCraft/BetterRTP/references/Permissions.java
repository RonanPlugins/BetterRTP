package me.SuperRonanCraft.BetterRTP.references;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class Permissions {

    private String pre = "betterrtp.";

    public boolean getUse(CommandSender sendi) {
        return perm(pre + "use", sendi);
    }

    boolean getEconomy(CommandSender sendi) {
        return perm(pre + "bypass.economy", sendi);
    }

    public boolean getBypassCooldown(CommandSender sendi) {
        return perm(pre + "bypass.cooldown", sendi);
    }

    public boolean getBypassDelay(CommandSender sendi) {
        return perm(pre + "bypass.delay", sendi);
    }

    public boolean getReload(CommandSender sendi) {
        return perm(pre + "reload", sendi);
    }

    public boolean getUpdate(CommandSender sendi) {
        return perm(pre + "updater", sendi);
    }

    public boolean getRtpOther(CommandSender sendi) {
        return perm(pre + "player", sendi);
    }

    public boolean getBiome(CommandSender sendi) {
        return (perm(pre + "biome", sendi));
    }

    public boolean getWorld(CommandSender sendi) {
        return (perm(pre + "world", sendi));
    }

    public boolean getSignCreate(CommandSender sendi) {
        return (perm(pre + "sign", sendi));
    }

    public boolean getAWorld(CommandSender sendi, String world) {
        if (perm(pre + "world.*", sendi))
            return true;
        else if (world == null) {
            for (World w : Bukkit.getWorlds())
                if (perm(pre + "world." + w.getName(), sendi))
                    return true;
        } else
            return perm(pre + "world." + world, sendi);
        return false;
    }

    private boolean perm(String str, CommandSender sendi) {
        return sendi.hasPermission(str);
    }
}
