package me.SuperRonanCraft.BetterRTPAddons.addons.parties.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.parties.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.HelperParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.PartyData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyCommand_Accept implements PartyCommands, PartyCommandsTabable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonParty addon) {
        Player p = (Player) sendi;
        if (HelperParty.isInParty(p)) {
            AddonParty.getInstance().msgs.getAccept_InAParty(sendi);
            return;
        }
        Player leader = null;
        if (args.length >= 3) {
            leader = Bukkit.getPlayer(args[2]);
            if (leader == null) {
                AddonParty.getInstance().msgs.getNotExist(sendi, args[2]);
                return;
            }
        } else {
            AddonParty.getInstance().msgs.usageAccept(sendi, label);
            return;
        }
        //List invites
        List<PartyData> invites = new ArrayList<>();
        for (PartyData party : AddonParty.getInstance().parties) {
            for (Player invited_player : party.getInvited())
                if (invited_player.equals(p))
                    invites.add(party);
        }

        if (!invites.isEmpty()) {
            for (PartyData party : invites) {
                if (party.getLeader().equals(leader)) {
                    party.add(p);
                    party.getInvited().remove(p);
                    //Success Message
                    AddonParty.getInstance().msgs.getAccept_Success(sendi, party.getLeader().getName());
                    party.getMembers().forEach((member, ready) -> { //Message of new party member
                        if (!member.equals(p))
                            AddonParty.getInstance().msgs.getAccept_Notification(member, p.getName());
                    });
                    //Party Leader Message
                    AddonParty.getInstance().msgs.getAccept_Notification(party.getLeader(), p.getName());
                }
            }
        } else
            AddonParty.getInstance().msgs.getAccept_NoInvites(sendi);
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args, AddonParty addon) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            List<Player> invites = new ArrayList<>();
            for (PartyData party : AddonParty.getInstance().parties) {
                for (Player invited_player : party.getInvited())
                    if (invited_player.equals(sendi))
                        invites.add(party.getLeader());
            }
            for (Player leader : invites) {
                if (leader.getName().toLowerCase().startsWith(args[2].toLowerCase()) && !leader.equals(sendi))
                    list.add(leader.getName());
            }
        }
        return list;
    }
}
