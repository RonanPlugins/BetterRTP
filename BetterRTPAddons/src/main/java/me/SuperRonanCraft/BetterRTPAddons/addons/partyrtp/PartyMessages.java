package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class PartyMessages implements AddonsMessages {
    private static final String preM = "Party.";

    public void getWarning(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Warning"));
    }

    public String getHelp() {
        return getLang().getString("Help.Portals");
    }

    //Invite

    public void getInvite(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Invite").replace("%name%", name));
    }

    //Usages

    public void usageRemove(CommandSender sendi, String label) {
        sms(sendi, getLang().getString(preM + "Usage.Remove").replace("%command%", label));
    }

    public void usageBase(CommandSender sendi, String label) {
        sms(sendi, getLang().getString(preM + "Usage.Base").replace("%command%", label));
    }
}
