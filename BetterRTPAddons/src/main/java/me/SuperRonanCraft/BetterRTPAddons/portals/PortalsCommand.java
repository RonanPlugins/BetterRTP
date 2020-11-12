package me.SuperRonanCraft.BetterRTPAddons.portals;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PortalsCommand implements RTPCommand {

    public String getName() {
        return "portals";
    }

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        System.out.println("Portals command!");
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @Override
    public boolean permission(CommandSender sendi) {
        return true;
    }
}
