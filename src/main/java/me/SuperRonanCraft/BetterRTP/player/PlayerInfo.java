package me.SuperRonanCraft.BetterRTP.player;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;

public class PlayerInfo {

    private final HashMap<Player, Inventory> invs = new HashMap<>();
    //private final HashMap<Player, RTP_INV_SETTINGS> invType = new HashMap<>();
    @Getter private final HashMap<Player, World> invWorld = new HashMap<>();
    @Getter private final HashMap<Player, RTP_INV_SETTINGS> invNextInv = new HashMap<>();
    //private final HashMap<Player, CooldownData> cooldown = new HashMap<>();
    @Getter private final HashMap<Player, Boolean> rtping = new HashMap<>();
    //private final HashMap<Player, List<Location>> previousLocations = new HashMap<>();
    //private final HashMap<Player, RTP_TYPE> rtpType = new HashMap<>();

    /*private void setInv(Player p, Inventory inv) {
        invs.put(p, inv);
    }*/

    /*private void setInvType(Player p, RTP_INV_SETTINGS type) {
        invType.put(p, type);
    }*/

    public void setInvWorld(Player p, World type) {
        invWorld.put(p, type);
    }

    public void setNextInv(Player p, RTP_INV_SETTINGS type) {
        invNextInv.put(p, type);
    }

    //--Logic--

    public Boolean playerExists(Player p) {
        return invs.containsKey(p);
    }

    private void unloadAll() {
        invs.clear();
        //invType.clear();
        invWorld.clear();
        invNextInv.clear();
        //cooldown.clear();
        rtping.clear();
        //previousLocations.clear();
    }

    private void unload(Player p) {
        clearInvs(p);
        //cooldown.remove(p);
        rtping.remove(p);
        //previousLocations.remove(p);
    }

    public void clearInvs(Player p) {
        invs.remove(p);
        //invType.remove(p);
        invWorld.remove(p);
        invNextInv.remove(p);
    }
}
