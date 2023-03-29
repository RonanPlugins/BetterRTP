package me.SuperRonanCraft.BetterRTP.references;

import com.griefdefender.api.permission.PermissionResult;
import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public enum PermissionNode {

    ADMIN("admin"),
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
    VERSION("version"),
    EDIT("edit"),
    LOCATION("location"),
    DEVELOPER("DEVELOPER_PERM"),
    ;

    private final String node;
    private static final String prefix = "betterrtp.";

    PermissionNode(String node) {
        this.node = prefix + node;
    }

    public boolean check(CommandSender sendi) {
        if (this == DEVELOPER)
            return sendi.getName().equalsIgnoreCase("SuperRonanCraft") || sendi.getName().equalsIgnoreCase("RonanCrafts");
        return BetterRTP.getInstance().getPerms().checkPerm(node, sendi);
    }

    public static boolean check(CommandSender sendi, String check) {
        return BetterRTP.getInstance().getPerms().checkPerm(check, sendi);
    }

    public static boolean getAWorld(CommandSender sendi, String world) {
        return getAWorldText(sendi, world).passed;
    }

    public static PermissionResult getAWorldText(CommandSender sendi, @NotNull String world) {
        String perm = prefix + "world.*";
        if (check(sendi, perm)) {
            return new PermissionResult(perm, true);
        } else {
            perm = prefix + "world." + world;
            if (check(sendi, perm))
                return new PermissionResult(perm, true);
        }
        return new PermissionResult(perm, false);
    }


    public static boolean getLocation(CommandSender sendi, String location) {
        return check(sendi, prefix + "location." + location);
    }

    public static boolean getPermissionGroup(CommandSender sendi, String group) {
        return check(sendi, prefix + "group." + group);
    }

    public static class PermissionResult {
        @Getter private final boolean passed;
        @Getter private final String string;
        PermissionResult(String string, boolean passed) {
            this.passed = passed;
            this.string = string;
        }
    }

}
