package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.player.commands.CommandTypes;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.references.file.Messages;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdHelp implements RTPCommand, RTPCommandHelpable {

    public void execute(CommandSender sendi, String label, String[] args) {
        Messages txt = BetterRTP.getInstance().getText();
        List<String> list = new ArrayList<>();
        list.add(txt.getHelpPrefix());
        list.add(txt.getHelpMain());
        for (CommandTypes cmd : CommandTypes.values())
            if (!cmd.isDebugOnly() || BetterRTP.getInstance().getSettings().debug)
                if (cmd.getCmd().permission(sendi))
                    if (cmd.getCmd() instanceof RTPCommandHelpable) {
                        String help = ((RTPCommandHelpable) cmd.getCmd()).getHelp();
                        list.add(help);
                    }
        for (int i = 0; i < list.size(); i++)
            list.set(i, list.get(i).replace("%command%", label));
        BetterRTP.getInstance().getText().sms(sendi, list);
//        if (pl.getPerms().getRtpOther(sendi))
//            pl.getText().getHelpPlayer(sendi, label);
//        if (sendi instanceof Player) {
//            if (pl.getPerms().getWorld(sendi))
//                pl.getText().getHelpWorld(sendi, label);
//        } else
//            pl.getText().getHelpWorld(sendi, label);
//        if (pl.getPerms().getReload(sendi))
//            pl.getText().getHelpReload(sendi, label);
        //if (pl.getPerms().getInfo(sendi))
        //    pl.getText().getHelpInfo(sendi);
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }


    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpHelp();
    }
}
