package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSettings implements RTPCommand {

    public void execute(CommandSender sendi, String label, String[] args) {
        Main.getInstance().getInvs().getInv(RTP_INV_SETTINGS.MAIN).show((Player) sendi);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getSettings(sendi);
    }
}
