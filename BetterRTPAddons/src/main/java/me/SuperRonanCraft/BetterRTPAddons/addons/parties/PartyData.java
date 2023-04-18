package me.SuperRonanCraft.BetterRTPAddons.addons.parties;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.Permissions;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyData {

    @Getter private final Player leader;
    @Getter private final HashMap<Player, Boolean> members = new HashMap<>();
    @Getter private final List<Player> invited = new ArrayList<>();

    public PartyData(Player leader) {
        this.leader = leader;
    }

    public boolean add(Player p) {
        if (!members.containsKey(p)) {
            members.put(p, false);
            return true;
        }
        return false;
    }

    public boolean invite(Player p) {
        if (!invited.contains(p) && !members.containsKey(p)) {
            invited.add(p);
            return true;
        }
        return false;
    }

    public boolean remove(Player p) {
        return members.remove(p) != null;
    }

    public boolean contains(Player p) {
        return leader.equals(p) || members.containsKey(p);
    }

    public boolean isLeader(Player p) {
        return this.leader.equals(p);
    }

    public boolean allReady() {
        return !members.containsValue(false);
    }

    public boolean readyUp(Player p) {
        if (members.containsKey(p) && !members.get(p)) {
            members.put(p, true);
            return true;
        }
        return false;
    }

    public void clear() {
        members.replaceAll((p, v) -> false);
    }

    public String getNotReady() {
        List<Player> notReady = new ArrayList<>();
        members.forEach((p, ready) -> {
            if (!ready) notReady.add(p);
        });
        StringBuilder notReady_str = new StringBuilder("[");
        notReady.forEach(p -> notReady_str.append(p.getName()));
        notReady_str.append("]");
        return notReady_str.toString();
    }

    public void tpAll(RTP_TeleportPostEvent e) {
        //HashMap<World, CooldownData> cooldownData = BetterRTP.getInstance().getPlayerDataManager().getData(getLeader()).getCooldowns();
        members.forEach((p, ready) -> {
            if (!p.equals(getLeader())) {
                Location loc = e.getLocation();
                //Async tp players
                PaperLib.teleportAsync(p, loc, PlayerTeleportEvent.TeleportCause.PLUGIN).thenRun(() -> {
                        /*BetterRTP.getInstance().getText().getSuccessBypass(p,
                                String.valueOf(loc.getBlockX()),
                                String.valueOf(loc.getBlockY()),
                                String.valueOf(loc.getBlockZ()),
                                loc.getWorld().getName(),
                                1);*/
                        BetterRTP.getInstance().getRTP().getTeleport().afterTeleport(p, loc, e.getWorldPlayer(), 0, e.getOldLocation(), e.getType());
                });
                //Set cooldowns
                if (!PermissionNode.BYPASS_COOLDOWN.check(p)) {
                    BetterRTP.getInstance().getCooldowns().add(p, loc.getWorld());
                    //BetterRTP.getInstance().getPlayerDataManager().getData(p).getCooldowns().put(loc.getWorld(), new CooldownData(p.getUniqueId(), System.currentTimeMillis(), loc.getWorld()));
                }
            }
        });
    }

    public boolean isMember(Player member) {
        return this.members.containsKey(member);
    }
}
