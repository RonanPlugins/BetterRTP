package me.SuperRonanCraft.BetterRTP.references;

import me.SuperRonanCraft.BetterRTP.references.depends.DepPerms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class Permissions {

    private final DepPerms depPerms = new DepPerms();

    public void register() {
        depPerms.register();
    }

    private final String pre = "betterrtp.";

    public boolean getUse(CommandSender sendi) {
        return perm(pre + "use", sendi);
    }

    public boolean getEconomy(CommandSender sendi) {
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

    public boolean getSettings(CommandSender sendi) {
        return perm(pre + "settings", sendi);
    }

    public boolean getInfo(CommandSender sendi) {
        return perm(pre + "info", sendi);
    }

    public boolean getUpdate(CommandSender sendi) {
        return perm(pre + "updater", sendi);
    }

    public boolean getRtpOther(CommandSender sendi) {
        return perm(pre + "player", sendi);
    }

    public boolean getBiome(CommandSender sendi) {
        return perm(pre + "biome", sendi);
    }

    public boolean getWorld(CommandSender sendi) {
        return perm(pre + "world", sendi);
    }

    public boolean getSignCreate(CommandSender sendi) {
        return perm(pre + "sign", sendi);
    }

    public boolean getTest(CommandSender sendi) {
        return perm(pre + "test", sendi);
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
        return depPerms.hasPerm(str, sendi);
    }
}
