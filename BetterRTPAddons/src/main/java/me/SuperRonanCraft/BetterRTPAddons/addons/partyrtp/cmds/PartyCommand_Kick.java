package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.HelperParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.PartyData;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.PartyMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand_Kick implements PartyCommands {

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
                            msgs.getKick_Kicked(sendi, member.getName());
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
}
