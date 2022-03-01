package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyMessages implements AddonsMessages {
    private static final String preM = "Party.";

    public void getOnlyLeader(CommandSender sendi, Player leader) {
        sms(sendi, getLang().getString(preM + "OnlyLeader").replace("%name%", leader.getName()));
    }

    public String getHelp() {
        return getLang().getString("Help.Party");
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

    //Members
    public void getMembers_NotReady(CommandSender sendi, String members) {
        sms(sendi, getLang().getString(preM + "Members.NotReady").replace("%members%", members));
    }

    public void getMembers_Ready(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Members.Ready"));
    }
    //Teleporting
    public void getTeleporting(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Teleport"));
    }
}
