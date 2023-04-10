package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RTPCommand {

    void execute(CommandSender sendi, String label, String[] args);

    List<String> tabComplete(CommandSender sendi, String[] args);

    @NotNull PermissionCheck permission();

    String getName();

    default boolean isDebugOnly() {
        return false;
    }

    default boolean enabled() {
        return true;
    };
}
