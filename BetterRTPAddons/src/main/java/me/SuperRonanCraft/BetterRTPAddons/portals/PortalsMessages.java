package me.SuperRonanCraft.BetterRTPAddons.portals;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import org.bukkit.command.CommandSender;

public class PortalsMessages implements AddonsMessages {
    private static String preM = "Messages";

    private Files.FILETYPE getLang() {
        return Files.FILETYPE.FLASHBACK;
    }

    public void getWarning(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Warning"));
    }
}
