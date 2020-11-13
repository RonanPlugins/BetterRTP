package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AddonsCommand implements RTPCommand {
    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        for (AddonsHandler.Addons addon : Main.getInstance().getAddonsHandler().addons) {
            sendi.sendMessage(addon.name());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @Override
    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getName() {
        return "addons";
    }
}
