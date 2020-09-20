package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginEnable {

    //In the case SoftDependencies plugins load after us!
    void enabled(PluginEnableEvent e) {
        String name = e.getPlugin().getName();
        //System.out.println(name);
        if (name.equalsIgnoreCase("WorldGuard"))
            Main.getInstance().getSettings().getsDepends().registerWorldguard();
        else if (name.equalsIgnoreCase("GriefPrevention"))
            Main.getInstance().getSettings().getsDepends().registerGriefPrevention();

    }

}
