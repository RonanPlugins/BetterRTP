package me.SuperRonanCraft.BetterRTP.player.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;

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
