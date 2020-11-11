package me.SuperRonanCraft.BetterRTPAddons;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerFlashback {

    Player p;
    Location oldLoc;

    PlayerFlashback(Player p, Location oldLoc, Long delayInTicks) {
        this.p = p;
        this.oldLoc = oldLoc;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), getTimedFlash(), delayInTicks);
    }

    private Runnable getTimedFlash() {
        return () -> {
            System.out.println("Player teleported back to old spot!");
            PaperLib.teleportAsync(p, oldLoc);
        };
    }
}
