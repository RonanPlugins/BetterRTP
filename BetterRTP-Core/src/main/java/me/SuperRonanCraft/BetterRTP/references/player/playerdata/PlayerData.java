package me.SuperRonanCraft.BetterRTP.references.player.playerdata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;

public class PlayerData {

    public boolean loading; //Is this players data loading?
    public final Player player;
    //Menus
    @Getter final PlayerData_Menus menu = new PlayerData_Menus();
    //Player Data
    @Getter final Map<World, CooldownData> cooldowns = new ConcurrentHashMap<>();
    //@Getter @Setter CooldownData globalCooldown;
    @Getter @Setter boolean rtping;
    @Getter @Setter int rtpCount;
    @Getter @Setter long globalCooldown;
    @Getter @Setter long invincibleEndTime;

    PlayerData(Player player) {
        this.player = player;
    }

    public void load(boolean joined) {
        //Setup Defaults
        //new TaskDownloadPlayerData(this, joined).start();
    }
}
