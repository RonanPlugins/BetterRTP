package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CmdReload implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "reload";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        BetterRTP.getInstance().reload(sendi);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    public boolean permission(CommandSender sendi) {
        return BetterRTP.getInstance().getPerms().getReload(sendi);
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpReload();
    }
}
