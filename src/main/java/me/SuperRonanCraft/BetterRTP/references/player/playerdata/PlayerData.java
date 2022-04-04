package me.SuperRonanCraft.BetterRTP.references.player.playerdata;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {

    public boolean loading; //Is this players data loading?
    public final Player player;
    //Menus
    @Getter final PlayerData_Menus menu = new PlayerData_Menus();
    //Player Data
    @Getter final HashMap<World, CooldownData> cooldowns = new HashMap<>();
    //@Getter @Setter CooldownData globalCooldown;
    @Getter @Setter boolean rtping;
    @Getter @Setter int rtpCount;

    PlayerData(Player player) {
        this.player = player;
    }

    public void load(boolean joined) {
        //Setup Defaults
        //new TaskDownloadPlayerData(this, joined).start();
    }
}
