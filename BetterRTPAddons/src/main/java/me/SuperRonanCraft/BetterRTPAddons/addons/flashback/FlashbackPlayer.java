package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class FlashbackPlayer {
    Player p;
    Location oldLoc;
    AddonFlashback plugin;
    List<Object> tasks = new ArrayList<>();

    public FlashbackPlayer(AddonFlashback plugin, Player p, Location oldLoc, Long seconds, HashMap<Long, String> warnings) {
        this.plugin = plugin;
        this.p = p;
        this.oldLoc = oldLoc;
        if (warnings != null)
            createTimers(seconds, orderMap(warnings));
        tasks.add(AsyncHandler.syncAtEntityLaterTask(p, runFlashback(seconds), 20L * seconds));
    }

    void createTimers(Long seconds, TreeMap<Long, String> warnings) {
        for (Map.Entry<Long, String> entry : warnings.entrySet()) {
            String str = entry.getValue();
            long time = seconds - entry.getKey();
            if (time >= 0)
                tasks.add(AsyncHandler.syncAtEntityLaterTask(p, runWarning(str), 20L * time));
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
            AsyncHandler.teleportAsync(p, oldLoc).thenAccept(success -> {
                if (success)
                    AsyncHandler.syncAtEntity(p, this::completed);
            });
        };
    }

    private Runnable runWarning(String msg) {
        return () -> plugin.msgs.sms(p, msg);
    }

    public void cancel() {
        for (Object task : tasks)
            AsyncHandler.cancelTask(task);
    }

    private void completed() {
        plugin.players.remove(this);
        plugin.database.removePlayer(p);
    }
}
