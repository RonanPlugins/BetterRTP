package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.WrappedTask;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

import java.util.concurrent.TimeUnit;

public class AsyncHandler {

    public static void async(Runnable runnable) {
        getFolia().runAsync(runnable);
        //Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), runnable);
    }

    public static void sync(Runnable runnable) {
        getFolia().runNextTick(runnable);
        //Bukkit.getScheduler().runTask(BetterRTP.getInstance(), runnable);
    }

    public static WrappedTask asyncLater(Runnable runnable, long ticks) {
        return getFolia().runLaterAsync(runnable, ticks * 50, TimeUnit.MILLISECONDS);
        //return Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), runnable, ticks);
    }
    public static WrappedTask syncLater(Runnable runnable, long ticks) {
        return getFolia().runLater(runnable, ticks * 50, TimeUnit.MILLISECONDS);
        //return Bukkit.getScheduler().runTaskLater(BetterRTP.getInstance(), runnable, ticks);
    }

    private static ServerImplementation getFolia() {
        return BetterRTP.getInstance().getFoliaHandler().get();
    }
}
