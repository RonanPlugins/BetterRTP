package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class FlashbackPlayer {
    Player p;
    Location oldLoc;
    AddonFlashback plugin;
    BukkitTask task;

    public FlashbackPlayer(AddonFlashback plugin, Player p, Location oldLoc, Long seconds) {
        this.plugin = plugin;
        this.p = p;
        this.oldLoc = oldLoc;
        this.task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), getTimedFlash(seconds), 20L * seconds);
    }

    private Runnable getTimedFlash(Long seconds) {
        if (plugin.database.setPlayer(p, oldLoc, System.currentTimeMillis() + (seconds * 1000)))
            p.sendMessage("A Database error has occurred!");
        return () -> {
            plugin.msgs.getWarning(p);
            PaperLib.teleportAsync(p, oldLoc);
            completed();
        };
    }

    public void cancel() {
        task.cancel();
    }

    private void completed() {
        plugin.players.remove(this);
    }
}
