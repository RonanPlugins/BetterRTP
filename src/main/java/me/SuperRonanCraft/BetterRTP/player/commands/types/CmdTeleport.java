package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdTeleport implements RTPCommand {

    //Label is the %command% placeholder in messages
    public static void teleport(CommandSender sendi, String label, World world, List<String> biomes) {
        if (sendi instanceof Player)
            HelperRTP.tp((Player) sendi, sendi, world, biomes, RTP_TYPE.COMMAND);
        else
            Message_RTP.sms(sendi, "Must be a player to use this command! Try '/" + label + " help'");
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        teleport(sendi, label, null, null);
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @Override
    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

}
