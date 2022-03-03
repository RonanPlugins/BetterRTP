package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import me.SuperRonanCraft.BetterRTPAddons.AddonsMessages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyMessages implements AddonsMessages {
    private static final String preM = "Party.";

    public void getOnlyLeader(CommandSender sendi, String leader) {
        sms(sendi, getLang().getString(preM + "OnlyLeader").replace("%name%", leader));
    }

    public String getHelp() {
        return getLang().getString("Help.Party");
    }

    public void getNotInParty(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "NotInParty"));
    }

    public void getNotExist(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "NotExist").replace("%name%", name));
    }
    //Invite

    public void getInvite_Invited(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Invite.Invited").replace("%name%", name));
    }

    public void getInvite_Notification(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Invite.Notification").replace("%name%", name));
    }

    public void getInvite_Already(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Invite.Already").replace("%name%", name));
    }

    //Usages

    public void usageBase(CommandSender sendi, String label) {
        sms(sendi, getLang().getString(preM + "Usage.Base").replace("%command%", label));
    }

    public void usageKick(CommandSender sendi, String label) {
        sms(sendi, getLang().getString(preM + "Usage.Kick").replace("%command%", label));
    }

    public void usageAccept(CommandSender sendi, String label) {
        sms(sendi, getLang().getString(preM + "Usage.Accept").replace("%command%", label));
    }

    public void usageInvite(CommandSender sendi, String label) {
        sms(sendi, getLang().getString(preM + "Usage.Invite").replace("%command%", label));
    }

    //Members
    public void getMembers_NotReady(CommandSender sendi, String members) {
        sms(sendi, getLang().getString(preM + "Members.NotReady").replace("%members%", members));
    }

    public void getMembers_NotInParty(CommandSender sendi, String name) {
        sms(sendi, getLang().getString(preM + "Members.NotInParty").replace("%name%", name));
    }

    public void getMembers_Ready(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Members.Ready"));
    }

    public void getMembers_LeaderLeft(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Members.LeaderLeft"));
    }

    public void getMembers_Leave(CommandSender sendi, String leader) {
        sms(sendi, getLang().getString(preM + "Members.Leave").replace("%name%", leader));
    }
    //Kick

    public void getKick_Kicked(CommandSender sendi, String kicked) {
        sms(sendi, getLang().getString(preM + "Kick.Kicked").replace("%name%", kicked));
    }

    public void getKick_Notification(CommandSender sendi, String leader) {
        sms(sendi, getLang().getString(preM + "Kick.Notification").replace("%name%", leader));
    }

    //Teleporting
    public void getTeleporting(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Teleport"));
    }
}
