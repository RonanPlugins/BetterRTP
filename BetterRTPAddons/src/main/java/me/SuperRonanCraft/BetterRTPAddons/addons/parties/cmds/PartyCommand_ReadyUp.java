package me.SuperRonanCraft.BetterRTPAddons.addons.parties.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.parties.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.HelperParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.parties.PartyData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand_ReadyUp implements PartyCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonParty addon) {
        Player p = (Player) sendi;
        PartyData party = HelperParty.getParty(p);
        if (party != null) {
            if (!party.isLeader(p)) {
                if (party.readyUp(p))
                    AddonParty.getInstance().msgs.getReady_Readied(sendi);
                else
                    AddonParty.getInstance().msgs.getReady_Already(sendi);
            } else
                AddonParty.getInstance().msgs.getReady_Leader(sendi);
        } else
            AddonParty.getInstance().msgs.getNotInParty(sendi);
    }
}
