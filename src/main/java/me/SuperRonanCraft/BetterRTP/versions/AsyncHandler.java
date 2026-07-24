package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public class AsyncHandler {

    public static void async(Runnable runnable) {
        getFolia().runAsync(task -> runnable.run());
    }

    public static void sync(Runnable runnable) {
        getFolia().runNextTick(task -> runnable.run());
    }

    public static void syncAtEntity(Entity entity, Runnable runnable) {
        syncAtEntity(entity, runnable, () -> { });
    }

    public static void syncAtEntity(Entity entity, Runnable runnable, Runnable retired) {
        getFolia().runAtEntityWithFallback(entity, task -> runnable.run(), retired);
    }

    public static void syncAtSender(CommandSender sender, Runnable runnable) {
        if (sender instanceof Entity entity)
            syncAtEntity(entity, runnable);
        else
            sync(runnable);
    }

    public static void syncAtLocation(Location location, Runnable runnable) {
        getFolia().runAtLocation(location, task -> runnable.run());
    }

    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        return getFolia().teleportAsync(entity, location);
    }

    public static WrappedTask asyncLater(Runnable runnable, long ticks) {
        return getFolia().runLaterAsync(runnable, ticks);
    }

    public static WrappedTask syncLater(Runnable runnable, long ticks) {
        return getFolia().runLater(runnable, ticks);
    }

    public static WrappedTask syncAtEntityLater(Entity entity, Runnable runnable, long ticks) {
        return getFolia().runAtEntityLater(entity, runnable, ticks);
    }

    public static Object syncAtEntityLaterTask(Entity entity, Runnable runnable, long ticks) {
        return syncAtEntityLater(entity, runnable, ticks);
    }

    public static void cancelTask(Object task) {
        if (task instanceof WrappedTask)
            ((WrappedTask) task).cancel();
    }

    private static ServerImplementation getFolia() {
        return BetterRTP.getInstance().getFoliaHandler().get();
    }
}
