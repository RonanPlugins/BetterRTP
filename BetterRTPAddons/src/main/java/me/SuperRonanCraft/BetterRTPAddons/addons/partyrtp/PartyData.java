package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyData {

    final Player leader;
    List<Player> members = new ArrayList<>();

    public PartyData(Player leader) {
        this.leader = leader;
    }

    public boolean contains(Player p) {
        return leader.equals(p) || members.contains(p);
    }
}
