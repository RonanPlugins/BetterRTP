package me.SuperRonanCraft.BetterRTP.player.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface RTPCommand {

    void execute(CommandSender sendi, String label, String[] args);

    List<String> tabComplete(CommandSender sendi, String[] args);

    boolean permission(CommandSender sendi);

    String getName();

    default boolean isDebugOnly() {
        return false;
    }
}
