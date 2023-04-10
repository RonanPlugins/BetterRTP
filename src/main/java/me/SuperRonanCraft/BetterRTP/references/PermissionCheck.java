package me.SuperRonanCraft.BetterRTP.references;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface PermissionCheck {

    static String getPrefix() {
        return "betterrtp.";
    }

    default boolean check(CommandSender sendi) {
        if (isDev())
            return sendi.getName().equalsIgnoreCase("SuperRonanCraft") || sendi.getName().equalsIgnoreCase("RonanCrafts");
        return BetterRTP.getInstance().getPerms().checkPerm(getNode(), sendi);
    }

    static boolean check(CommandSender sendi, String check) {
        return BetterRTP.getInstance().getPerms().checkPerm(check, sendi);
    }

    static boolean getAWorld(CommandSender sendi, String world) {
        return getAWorldText(sendi, world).passed;
    }

    static PermissionResult getAWorldText(CommandSender sendi, @NotNull String world) {
        String perm = getPrefix() + "world.*";
        if (check(sendi, perm)) {
            return new PermissionResult(perm, true);
        } else {
            perm = getPrefix() + "world." + world;
            if (check(sendi, perm))
                return new PermissionResult(perm, true);
        }
        return new PermissionResult(perm, false);
    }

    static boolean getLocation(CommandSender sendi, String location) {
        return check(sendi, getPrefix() + "location." + location);
    }

    static boolean getPermissionGroup(CommandSender sendi, String group) {
        return check(sendi, getPrefix() + "group." + group);
    }

    boolean isDev();

    String getNode();

    class PermissionResult {
        @Getter private final boolean passed;
        @Getter private final String string;
        PermissionResult(String string, boolean passed) {
            this.passed = passed;
            this.string = string;
        }
    }
}