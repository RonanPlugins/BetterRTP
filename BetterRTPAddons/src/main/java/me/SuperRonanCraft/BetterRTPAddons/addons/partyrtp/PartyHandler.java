package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_SettingUpEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PartyHandler implements Listener {

    //Dissallow players from teleporting unless all party members are ready
    @EventHandler
    public void onRTP(RTP_SettingUpEvent e) {
        PartyData party = HelperParty.getParty(e.getPlayer());
        if (party != null) {
            if (party.isLeader(e.getPlayer())) {
                if (!party.allReady()) { //NOT ALL READY
                    AddonParty.getInstance().msgs.getMembers_NotReady(e.getPlayer(), party.getNotReady());
                    e.setCancelled(true);
                } //else rtp!
            } else {
                AddonParty.getInstance().msgs.getOnlyLeader(e.getPlayer(), party.getLeader().getName());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRTP_end(RTP_TeleportPostEvent e) {
        PartyData party = HelperParty.getParty(e.getPlayer());
        if (party != null && party.isLeader(e.getPlayer())) {
            party.tpAll(e);
        }
    }

}
