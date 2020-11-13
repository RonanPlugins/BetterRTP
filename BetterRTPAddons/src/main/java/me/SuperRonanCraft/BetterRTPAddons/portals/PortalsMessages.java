package me.SuperRonanCraft.BetterRTPAddons.portals;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import org.bukkit.command.CommandSender;

public class PortalsMessages implements AddonsMessages {
    private static final String preM = "Messages.";

    private Files.FILETYPE getLang() {
        return Files.FILETYPE.PORTALS;
    }

    public void getWarning(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Warning"));
    }

    public String getHelp() {
        return getLang().getString(preM + "Help");
    }
}
