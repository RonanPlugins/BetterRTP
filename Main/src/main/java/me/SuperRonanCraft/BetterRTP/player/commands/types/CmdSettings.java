package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSettings implements RTPCommand, RTPCommandHelpable {

    public void execute(CommandSender sendi, String label, String[] args) {
        if (sendi instanceof Player)
            Main.getInstance().getInvs().getInv(RTP_INV_SETTINGS.MAIN).show((Player) sendi);
        else
            Main.getInstance().getCmd().msgNotPlayer(sendi, label);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getSettings(sendi);
    }

    @Override
    public String getHelp() {
        return Main.getInstance().getText().getHelpSettings();
    }
}
