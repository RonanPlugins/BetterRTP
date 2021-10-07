package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.AddonParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.HelperParty;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.PartyData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyCommand_Invite implements PartyCommands, PartyCommandsTabable {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonParty addon) {
        Player p = (Player) sendi;
        PartyData party = null;
        if (HelperParty.isInParty(p))
            party = HelperParty.getParty(p);
        else
            party = new PartyData(p);

    }

    //rtp party invite [args]
    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args, AddonParty addon) {
        List<String> list = new ArrayList<>();
        if (args.length == 4) {
            for (World world : Bukkit.getWorlds())
                if (world.getName().toLowerCase().startsWith(args[3].toLowerCase()))
                    list.add(world.getName());
        }
        return list;
    }
}
