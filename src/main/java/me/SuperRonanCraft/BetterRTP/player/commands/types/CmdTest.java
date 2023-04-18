package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//Meant to just test particles and effects without actually rtp'ing around the world
public class CmdTest implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "test";
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        if (sendi instanceof Player) {
            Player p = (Player) sendi;
            BetterRTP.getInstance().getRTP().getTeleport().afterTeleport(p, p.getLocation(),
                    HelperRTP.getPlayerWorld(new RTPSetupInformation(p.getWorld(), p, p, false)), 0, p.getLocation(), RTP_TYPE.TEST);
        } else
            sendi.sendMessage("Console is not able to execute this command! Try '/rtp help'");
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @NotNull @Override
    public PermissionNode permission() {
        return PermissionNode.ADMIN;
    }

    @Override
    public String getHelp() {
        return MessagesHelp.TEST.get();
    }

    @Override
    public boolean isDebugOnly() {
        return true;
    }
}
