package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdHelp implements RTPCommand {

    public void execute(CommandSender sendi, String label, String[] args) {
        Main pl = Main.getInstance();
        pl.getText().getHelpList(sendi, label);
        if (pl.getPerms().getRtpOther(sendi))
            pl.getText().getHelpPlayer(sendi, label);
        if (sendi instanceof Player) {
            if (pl.getPerms().getWorld(sendi))
                pl.getText().getHelpWorld(sendi, label);
        } else
            pl.getText().getHelpWorld(sendi, label);
        if (pl.getPerms().getReload(sendi))
            pl.getText().getHelpReload(sendi, label);
        //if (pl.getPerms().getInfo(sendi))
        //    pl.getText().getHelpInfo(sendi);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }


    public boolean permission(CommandSender sendi) {
        return true;
    }
}
