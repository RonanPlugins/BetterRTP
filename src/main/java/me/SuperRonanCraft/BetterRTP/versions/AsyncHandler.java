package me.SuperRonanCraft.BetterRTP.versions;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class AsyncHandler {

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), runnable);
    }

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(BetterRTP.getInstance(), runnable);
    }

    public static BukkitTask asyncLater(Runnable runnable, long ticks) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), runnable, ticks);
    }
    public static BukkitTask syncLater(Runnable runnable, long ticks) {
        return Bukkit.getScheduler().runTaskLater(BetterRTP.getInstance(), runnable, ticks);
    }
}
