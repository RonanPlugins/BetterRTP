package me.SuperRonanCraft.BetterRTP.references.systems.playerdata;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import org.bukkit.entity.Player;

public class PlayerData {

    public boolean loading; //Is this players data loading?
    public final Player player;
    //Menus
    @Getter final PlayerData_Menus menu = new PlayerData_Menus();
    //Player Data
    @Getter @Setter CooldownData cooldown;
    @Getter @Setter boolean rtping;

    PlayerData(Player player) {
        this.player = player;
    }

    public void load(boolean joined) {
        //Setup Defaults
        //new TaskDownloadPlayerData(this, joined).start();
    }
}
