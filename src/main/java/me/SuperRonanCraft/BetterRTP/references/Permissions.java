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
        return checkPerm(pre + "use", sendi);
    }

    public boolean getBypassEconomy(CommandSender sendi) {
        return checkPerm(pre + "bypass.economy", sendi);
    }

    public boolean getBypassHunger(CommandSender sendi) {
        return checkPerm(pre + "bypass.hunger", sendi);
    }

    public boolean getBypassCooldown(CommandSender sendi) {
        return checkPerm(pre + "bypass.cooldown", sendi);
    }

    public boolean getBypassDelay(CommandSender sendi) {
        return checkPerm(pre + "bypass.delay", sendi);
    }

    public boolean getReload(CommandSender sendi) {
        return checkPerm(pre + "reload", sendi);
    }

    public boolean getSettings(CommandSender sendi) {
        return checkPerm(pre + "settings", sendi);
    }

    public boolean getInfo(CommandSender sendi) {
        return checkPerm(pre + "info", sendi);
    }

    public boolean getUpdate(CommandSender sendi) {
        return checkPerm(pre + "updater", sendi);
    }

    public boolean getRtpOther(CommandSender sendi) {
        return checkPerm(pre + "player", sendi);
    }

    public boolean getBiome(CommandSender sendi) {
        return checkPerm(pre + "biome", sendi);
    }

    public boolean getWorld(CommandSender sendi) {
        return checkPerm(pre + "world", sendi);
    }

    public boolean getSignCreate(CommandSender sendi) {
        return checkPerm(pre + "sign", sendi);
    }

    public boolean getTest(CommandSender sendi) {
        return checkPerm(pre + "test", sendi);
    }

    public boolean getVersion(CommandSender sendi) {
        return checkPerm(pre + "version", sendi);
    }

    public boolean getAWorld(CommandSender sendi, String world) {
        if (checkPerm(pre + "world.*", sendi))
            return true;
        else if (world == null) {
            for (World w : Bukkit.getWorlds())
                if (checkPerm(pre + "world." + w.getName(), sendi))
                    return true;
        } else
            return checkPerm(pre + "world." + world, sendi);
        return false;
    }

    public boolean getEdit(CommandSender sendi) {
        return checkPerm(pre + "edit", sendi);
    }

    public boolean getLocation(CommandSender sendi) {
        return checkPerm(pre + "location", sendi);
    }

    public boolean getPermissionGroup(CommandSender sendi, String group) {
        return checkPerm(pre + "group." + group, sendi);
    }

    public boolean checkPerm(String str, CommandSender sendi) {
        return depPerms.hasPerm(str, sendi);
    }
}
