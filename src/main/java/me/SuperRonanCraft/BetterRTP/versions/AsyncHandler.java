package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.wrapper.WrappedTask;

import java.util.concurrent.TimeUnit;

public class AsyncHandler {

    public static void async(Runnable runnable) {
        FoliaHandler.get().runAsync(runnable);
        //Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), runnable);
    }

    public static void sync(Runnable runnable) {
        FoliaHandler.get().runNextTick(runnable);
        //Bukkit.getScheduler().runTask(BetterRTP.getInstance(), runnable);
    }

    public static WrappedTask asyncLater(Runnable runnable, long ticks) {
        return FoliaHandler.get().runLaterAsync(runnable, ticks * 50, TimeUnit.MILLISECONDS);
        //return Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), runnable, ticks);
    }
    public static WrappedTask syncLater(Runnable runnable, long ticks) {
        return FoliaHandler.get().runLaterAsync(runnable, ticks * 50, TimeUnit.MILLISECONDS);
        //return Bukkit.getScheduler().runTaskLater(BetterRTP.getInstance(), runnable, ticks);
    }
}
