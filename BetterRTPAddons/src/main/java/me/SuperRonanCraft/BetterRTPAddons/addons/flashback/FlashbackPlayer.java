package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import io.papermc.lib.PaperLib;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class FlashbackPlayer {
    Player p;
    Location oldLoc;
    AddonFlashback plugin;
    List<BukkitTask> tasks = new ArrayList<>();

    public FlashbackPlayer(AddonFlashback plugin, Player p, Location oldLoc, Long seconds, HashMap<Long, String> warnings) {
        this.plugin = plugin;
        this.p = p;
        this.oldLoc = oldLoc;
        if (warnings != null)
            createTimers(seconds, orderMap(warnings));
        tasks.add(Bukkit.getScheduler().runTaskLater(Main.getInstance(), runFlashback(seconds), 20L * seconds));
    }

    void createTimers(Long seconds, TreeMap<Long, String> warnings) {
        for (Map.Entry<Long, String> entry : warnings.entrySet()) {
            String str = entry.getValue();
            long time = seconds - entry.getKey();
            if (time >= 0)
                tasks.add(Bukkit.getScheduler().runTaskLater(Main.getInstance(), runWarning(str), 20L * time));
        }
    }

    TreeMap<Long, String> orderMap(HashMap<Long, String> warnings) {
        return new TreeMap<>(warnings);
    }

    private Runnable runFlashback(Long seconds) {
        if (!plugin.database.setPlayer(p, oldLoc, System.currentTimeMillis() + (seconds * 1000)))
            p.sendMessage("A Database error has occurred!");
        return () -> {
            plugin.msgs.getWarning(p);
            PaperLib.teleportAsync(p, oldLoc);
            completed();
        };
    }

    private Runnable runWarning(String msg) {
        return () -> plugin.msgs.sms(p, msg);
    }

    public void cancel() {
        for (BukkitTask task : tasks)
            task.cancel();
    }

    private void completed() {
        plugin.players.remove(this);
        plugin.database.removePlayer(p);
    }
}
