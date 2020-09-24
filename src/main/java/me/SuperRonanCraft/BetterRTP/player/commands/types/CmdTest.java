package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

//Meant to just test particles and effects without actually rtp'ing around the world
public class CmdTest implements RTPCommand, RTPCommandHelpable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (sendi instanceof Player) {
            Player p = (Player) sendi;
            Main.getInstance().getRTP().getTeleport().afterTeleport(p, p.getLocation(), 0, 0);
        } else
            sendi.sendMessage("Console is not able to execute this command! Try '/rtp help'");
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @Override
    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getTest(sendi);
    }

    @Override
    public String getHelp() {
        return Main.getInstance().getText().getHelpTest();
    }

}
