package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.entity.Entity;

public class AsyncHandler {

    public static void async(Runnable runnable) {
        getFolia().runAsync(task -> runnable.run());
        //Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), runnable);
    }

    public static void sync(Runnable runnable) {
        getFolia().runNextTick(task -> runnable.run());
        //Bukkit.getScheduler().runTask(BetterRTP.getInstance(), runnable);
    }

    public static void syncAtEntity(Entity entity, Runnable runnable) {
        getFolia().runAtEntity(entity, task -> runnable.run());
        //Bukkit.getScheduler().runTask(BetterRTP.getInstance(), runnable);
    }

    public static WrappedTask asyncLater(Runnable runnable, long ticks) {
        return getFolia().runLaterAsync(runnable, ticks);
        //return Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), runnable, ticks);
    }
    public static WrappedTask syncLater(Runnable runnable, long ticks) {
        return getFolia().runLater(runnable, ticks);
        //return Bukkit.getScheduler().runTaskLater(BetterRTP.getInstance(), runnable, ticks);
    }

    private static ServerImplementation getFolia() {
        return BetterRTP.getInstance().getFoliaHandler().get();
    }
}
