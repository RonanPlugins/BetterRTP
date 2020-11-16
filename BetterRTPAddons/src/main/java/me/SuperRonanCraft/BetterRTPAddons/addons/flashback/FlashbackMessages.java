package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import org.bukkit.command.CommandSender;

public class FlashbackMessages implements AddonsMessages {
    private static String preM = "Messages";

    private Files.FILETYPE getLang() {
        return Files.FILETYPE.FLASHBACK;
    }

    public void getWarning(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Warning"));
    }
}
