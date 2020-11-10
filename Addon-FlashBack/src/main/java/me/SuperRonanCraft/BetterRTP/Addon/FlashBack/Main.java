package me.SuperRonanCraft.BetterRTP.Addon.FlashBack;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        BetterRTP.getInstance().getEvents().addListener(new Events());
    }
}
