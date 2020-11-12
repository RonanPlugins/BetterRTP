package me.SuperRonanCraft.BetterRTPAddons.flashBack;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import org.bukkit.command.CommandSender;

public class MessagesFlashback implements AddonsMessages {
    private static String preM = "Messages";

    private Files.FILETYPE getLang() {
        return Files.FILETYPE.EFFECTS;
    }

    public void getWarning(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Warning"));
    }
}
