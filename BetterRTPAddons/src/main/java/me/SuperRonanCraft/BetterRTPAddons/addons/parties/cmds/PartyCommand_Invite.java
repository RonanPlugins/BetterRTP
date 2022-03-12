package me.SuperRonanCraft.BetterRTPAddons.addons.parties.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.parties.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.HelperParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.PartyData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyCommand_Invite implements PartyCommands, PartyCommandsTabable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonParty addon) {
        Player p = (Player) sendi;
        if (args.length >= 3) {
            Player member = Bukkit.getPlayer(args[2]);
            if (member != null && !member.equals(p)) {
                PartyData party = HelperParty.getParty(p);
                if (party == null) {
                    party = new PartyData(p);
                    AddonParty.getInstance().parties.add(party);
                }
                if (party.isLeader(p)) {
                    if (party.invite(member)) {
                        AddonParty.getInstance().msgs.getInvite_Invited(sendi, member.getName());
                        AddonParty.getInstance().msgs.getInvite_Notification(member, p.getName());
                    } else
                        AddonParty.getInstance().msgs.getInvite_Already(sendi, member.getName());
                } else
                    AddonParty.getInstance().msgs.getOnlyLeader(sendi, party.getLeader().getName());
            } else
                AddonParty.getInstance().msgs.getNotExist(sendi, args[2]);
        } else
            AddonParty.getInstance().msgs.usageInvite(sendi, label);

    }

    //rtp party invite [args]
    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args, AddonParty addon) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().toLowerCase().startsWith(args[2].toLowerCase()) && !p.equals(sendi))
                    list.add(p.getName());
        }
        return list;
    }
}
