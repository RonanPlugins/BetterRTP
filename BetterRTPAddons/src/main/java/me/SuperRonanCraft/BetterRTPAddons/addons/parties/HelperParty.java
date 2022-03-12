package me.SuperRonanCraft.BetterRTPAddons.addons.parties;

import org.bukkit.entity.Player;

import java.util.List;

public class HelperParty {

    public static boolean isInParty(Player p) {
        List<PartyData> parties = getPl().parties;
        for (PartyData party : parties)
            if (party.contains(p))
                return true;
        return false;
    }

    public static PartyData getParty(Player p) {
        List<PartyData> parties = getPl().parties;
        for (PartyData party : parties)
            if (party.contains(p))
                return party;
        return null;
    }

    private static AddonParty getPl() {
        return AddonParty.getInstance();
    }

}
