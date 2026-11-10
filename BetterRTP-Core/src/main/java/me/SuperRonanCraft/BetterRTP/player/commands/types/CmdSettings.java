package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CmdSettings implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "settings";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        if (sendi instanceof Player)
            BetterRTP.getInstance().getInvs().getInv(RTP_INV_SETTINGS.MAIN).show((Player) sendi);
        else
            Message_RTP.sms(sendi, "Must be a player to use this command! Try '/" + label + " help'");
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @NotNull public PermissionNode permission() {
        return PermissionNode.SETTINGS;
    }

    @Override
    public String getHelp() {
        return MessagesHelp.SETTINGS.get();
    }
}
