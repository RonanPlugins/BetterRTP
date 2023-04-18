package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.Commands;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Info;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesUsage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CmdBiome implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "biome";
    }

    //rtp biome <biome1, biome2...>
    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length >= 2)
            CmdTeleport.teleport(sendi, label, null, HelperRTP_Info.getBiomes(args, 1, sendi));
        else
            usage(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2)
            HelperRTP_Info.addBiomes(list, args);
        return list;
    }

    @NotNull public PermissionNode permission() {
        return PermissionNode.BIOME;
    }

    public void usage(CommandSender sendi, String label) {
        MessagesUsage.BIOME.send(sendi, label);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.BIOME.get();
    }
}
