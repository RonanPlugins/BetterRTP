package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import org.bukkit.command.CommandSender;

public class FlashbackMessages implements AddonsMessages {
    private static String preM = "Flashback.";

    public void getWarning(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Warning"));
    }
}
