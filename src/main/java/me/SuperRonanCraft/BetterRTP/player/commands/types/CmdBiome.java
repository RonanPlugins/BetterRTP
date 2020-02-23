package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CmdBiome implements RTPCommand {

    //rtp biome <biome1, biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2)
            Main.getInstance().getCmd().rtp(sendi, label, null, Main.getInstance().getCmd().getBiomes(args, 1, sendi));
        else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getBiome(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        Main.getInstance().getText().getUsageBiome(sendi, label);
    }
}
