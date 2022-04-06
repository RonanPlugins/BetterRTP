package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSettings implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "settings";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        if (sendi instanceof Player)
            BetterRTP.getInstance().getInvs().getInv(RTP_INV_SETTINGS.MAIN).show((Player) sendi);
        else
            BetterRTP.getInstance().getText().getNotPlayer(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNode.SETTINGS.check(sendi);
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpSettings();
    }
}
