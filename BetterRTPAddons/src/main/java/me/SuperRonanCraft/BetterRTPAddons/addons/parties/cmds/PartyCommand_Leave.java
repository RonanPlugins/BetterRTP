package me.SuperRonanCraft.BetterRTPAddons.addons.parties.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.parties.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.HelperParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.PartyData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand_Leave implements PartyCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonParty addon) {
        Player p = (Player) sendi;
        PartyData party = HelperParty.getParty(p);
        if (party != null) {
            if (party.isLeader(p)) { //Leader deleted party
                AddonParty.getInstance().parties.remove(party);
                for (Player member : party.getMembers().keySet())
                    AddonParty.getInstance().msgs.getMembers_LeaderLeft(member);
            } else {
                party.remove(p);
                AddonParty.getInstance().msgs.getMembers_Leave(sendi, party.getLeader().getName());
            }
        } else
            AddonParty.getInstance().msgs.getNotInParty(p);
    }
}
