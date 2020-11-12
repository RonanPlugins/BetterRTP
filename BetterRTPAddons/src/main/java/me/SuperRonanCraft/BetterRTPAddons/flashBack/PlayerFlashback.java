package me.SuperRonanCraft.BetterRTPAddons.flashBack;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerFlashback {
    Player p;
    Location oldLoc;
    AddonFlashback plugin;

    public PlayerFlashback(AddonFlashback plugin, Player p, Location oldLoc, Long delayInTicks) {
        this.plugin = plugin;
        this.p = p;
        this.oldLoc = oldLoc;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), getTimedFlash(), delayInTicks);
    }

    private Runnable getTimedFlash() {
        return () -> {
            plugin.msgs.getWarning(p);
            PaperLib.teleportAsync(p, oldLoc);
        };
    }

}
