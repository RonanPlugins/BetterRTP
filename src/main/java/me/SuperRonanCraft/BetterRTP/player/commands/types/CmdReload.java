package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CmdReload implements RTPCommand, RTPCommandHelpable {

    public void execute(CommandSender sendi, String label, String[] args) {
        Main.getInstance().reload(sendi);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getReload(sendi);
    }

    @Override
    public String getHelp() {
        return Main.getInstance().getText().getHelpReload();
    }
}
