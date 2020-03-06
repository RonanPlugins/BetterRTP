package me.SuperRonanCraft.BetterRTP.player;

import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class PlayerInfo {

    private HashMap<Player, Inventory> invs = new HashMap<>();
    private HashMap<Player, RTP_INV_SETTINGS> invType = new HashMap<>();
    private HashMap<Player, World> invWorld = new HashMap<>();
    private HashMap<Player, RTP_INV_SETTINGS> invNextInv = new HashMap<>();

    public Inventory getInv(Player p) {
        return invs.get(p);
    }

    public RTP_INV_SETTINGS getInvType(Player p) {
        return invType.get(p);
    }

    public World getInvWorld(Player p) {
        return invWorld.get(p);
    }

    public RTP_INV_SETTINGS getNextInv(Player p) {
        return invNextInv.get(p);
    }

    public void setInv(Player p, Inventory inv) {
        invs.put(p, inv);
    }

    public void setInvType(Player p, RTP_INV_SETTINGS type) {
        invType.put(p, type);
    }

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

    public void clear(Player p) {
        invs.remove(p);
        invType.remove(p);
        //invWorld.remove(p);
    }

}
