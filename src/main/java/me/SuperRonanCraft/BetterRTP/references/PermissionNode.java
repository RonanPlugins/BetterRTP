package me.SuperRonanCraft.BetterRTP.references;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.depends.DepPerms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public enum PermissionNode {

    USE("use"),
    BYPASS_ECONOMY("bypass.economy"),
    BYPASS_HUNGER("bypass.hunger"),
    BYPASS_COOLDOWN("bypass.cooldown"),
    BYPASS_DELAY("bypass.delay"),
    RELOAD("reload"),
    SETTINGS("settings"),
    INFO("info"),
    UPDATER("updater"),
    RTP_OTHER("player"),
    BIOME("biome"),
    WORLD("world"),
    SIGN_CREATE("sign"),
    TEST("test"),
    VERSION("version"),
    EDIT("edit"),
    LOCATION("location"),
    ;

    private final String node;
    private static final String prefix = "betterrtp.";

    PermissionNode(String node) {
        this.node = prefix + node;
    }

    public boolean check(CommandSender sendi) {
        return BetterRTP.getInstance().getPerms().checkPerm(node, sendi);
    }

    public static boolean check(CommandSender sendi, String check) {
        return BetterRTP.getInstance().getPerms().checkPerm(check, sendi);
    }

    public static boolean getAWorld(CommandSender sendi, String world) {
        if (check(sendi, prefix + "world.*"))
            return true;
        else if (world == null) {
            for (World w : Bukkit.getWorlds())
                if (check(sendi, prefix + "world." + w.getName()))
                    return true;
        } else
            return check(sendi, prefix + "world." + world);
        return false;
    }


    public static boolean getLocation(CommandSender sendi, String location) {
        return check(sendi, prefix + "location." + location);
    }

    public static boolean getPermissionGroup(CommandSender sendi, String group) {
        return check(sendi, prefix + "group." + group);
    }

}
