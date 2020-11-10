package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.Commands;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdBiome implements RTPCommand, RTPCommandHelpable {

    //rtp biome <biome1, biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2)
            BetterRTP.getInstance().getCmd().rtp(sendi, label, null, BetterRTP.getInstance().getCmd().getBiomes(args, 1, sendi));
        else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2)
            getCmd().addBiomes(list, args);
        return list;
    }

    public boolean permission(CommandSender sendi) {
        return BetterRTP.getInstance().getPerms().getBiome(sendi);
    }

    public void usage(CommandSender sendi, String label) {
        BetterRTP.getInstance().getText().getUsageBiome(sendi, label);
    }

    private Commands getCmd() {
        return BetterRTP.getInstance().getCmd();
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpBiome();
    }
}
