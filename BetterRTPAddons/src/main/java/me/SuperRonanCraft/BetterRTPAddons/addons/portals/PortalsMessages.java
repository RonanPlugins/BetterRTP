package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import org.bukkit.command.CommandSender;

public class PortalsMessages implements AddonsMessages {
    private static final String preM = "Portals.";

    public void getWarning(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Warning"));
    }

    public String getHelp() {
        return getLang().getString("Help.Portals");
    }

    //Create

    public void getCreateConfirm(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Created.Confirm").replace("%name%", name));
    }

    public void getCreateInvalid(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Created.Invalid"));
    }

    public void getCreateName(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Created.Name"));
    }

    public void getCreateDuplicate(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Created.Duplicate"));
    }

    //Location

    public void getLocation_1(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Location.1"));
    }

    public void getLocation_2(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Location.2"));
    }

    public void getLocation_Look(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Location.Look"));
    }

    //Remove

    public void getRemoveConfirm(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Remove.Confirm").replace("%name%", name));
    }

    public void getRemoveNone(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Remove.None").replace("%name%", name));
    }

    public void usageRemove(CommandSender sendi, String label) {
        sms(sendi, getLang().getString(preM + "Usage.Remove").replace("%command%", label));
    }
}
