package me.SuperRonanCraft.BetterRTP.player;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInfo {

    @Getter private final HashMap<Player, Inventory> invs = new HashMap<>();
    @Getter private final HashMap<Player, RTP_INV_SETTINGS> invType = new HashMap<>();
    @Getter private final HashMap<Player, World> invWorld = new HashMap<>();
    @Getter private final HashMap<Player, RTP_INV_SETTINGS> invNextInv = new HashMap<>();
    @Getter private final HashMap<Player, CooldownData> cooldown = new HashMap<>();
    @Getter private final HashMap<Player, Boolean> rtping = new HashMap<>();
    //private final HashMap<Player, RTP_TYPE> rtpType = new HashMap<>();

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

    public void unloadAll() {
        invs.clear();
        invType.clear();
        invWorld.clear();
        invNextInv.clear();
        cooldown.clear();
        rtping.clear();
    }

    public void unload(Player p) {
        clearInvs(p);
        cooldown.remove(p);
        rtping.remove(p);
    }

    public void clearInvs(Player p) {
        invs.remove(p);
        invType.remove(p);
        invWorld.remove(p);
        invNextInv.remove(p);
    }
}
