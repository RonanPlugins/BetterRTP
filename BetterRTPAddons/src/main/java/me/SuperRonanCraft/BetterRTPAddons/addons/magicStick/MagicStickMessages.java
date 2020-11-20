package me.SuperRonanCraft.BetterRTPAddons.addons.magicStick;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import org.bukkit.command.CommandSender;

public class MagicStickMessages implements AddonsMessages {
    private static final String preM = "MagicStick.";

    public String getHelp() {
        return getLang().getString("Help.MagicStick");
    }

    //Give
    public void getGive(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Give").replace("%name%", name));
    }

    public void getGiven(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Given"));
    }

    public void getPlayerError(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Player").replace("%player%", name));
    }
}
