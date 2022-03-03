package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.HelperParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.PartyData;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.PartyMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyCommand_Kick implements PartyCommands, PartyCommandsTabable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonParty addon) {
        Player p = (Player) sendi;
        PartyMessages msgs = AddonParty.getInstance().msgs;
        PartyData party = HelperParty.getParty(p);
        if (party != null) {
            if (party.isLeader(p)) {
                if (args.length >= 3) {
                    Player member = Bukkit.getPlayer(args[2]);
                    if (member != null) {
                        if (party.isMember(member)) {
                            party.remove(member);
                            msgs.getKick_Kicked(sendi, member.getName());
                            msgs.getKick_Notification(member, sendi.getName());
                        } else
                            msgs.getMembers_NotInParty(sendi, member.getName());
                    } else
                        msgs.getNotExist(sendi, args[2]);
                } else
                    msgs.usageKick(sendi, label);
            } else
                msgs.getOnlyLeader(sendi, party.getLeader().getName());
        } else
            msgs.getNotInParty(sendi);
    }

    @Override public List<String> tabComplete(CommandSender sendi, String[] args, AddonParty addon) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            PartyData party = HelperParty.getParty((Player) sendi);
            if (party != null)
                for (Player p : party.getMembers().keySet())
                    if (p.getName().toLowerCase().startsWith(args[2].toLowerCase()) && !p.equals(sendi))
                        list.add(p.getName());
        }
        return list;
    }
}
