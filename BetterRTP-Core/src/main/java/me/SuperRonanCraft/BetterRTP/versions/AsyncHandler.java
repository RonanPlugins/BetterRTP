package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Chunk;
import io.papermc.lib.PaperLib;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncHandler {

    public static void async(Runnable runnable) {
        getFolia().runAsync(task -> runnable.run());
    }

    public static void sync(Runnable runnable) {
        getFolia().runNextTick(task -> runnable.run());
    }

    public static void syncAtLocation(Location location, Runnable runnable) {
        getFolia().runAtLocation(location, task -> runnable.run());
    }

    /** Load and inspect a chunk on its owning region, including PaperLib's sync fallback. */
    public static CompletableFuture<Void> atLoadedChunk(Location location, Runnable action) {
        return atLoadedChunk(task -> syncAtLocation(location, task),
                () -> PaperLib.getChunkAtAsync(location), action);
    }

    static CompletableFuture<Void> atLoadedChunk(Consumer<Runnable> scheduler,
            Supplier<CompletableFuture<Chunk>> loader, Runnable action) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        schedule(scheduler, () -> loader.get().whenComplete((chunk, error) -> {
            if (error != null) {
                result.completeExceptionally(error);
            } else {
                // Future completion alone does not guarantee ownership of the destination region.
                schedule(scheduler, () -> {
                    action.run();
                    result.complete(null);
                }, result);
            }
        }), result);
        return result;
    }

    private static void schedule(Consumer<Runnable> scheduler, Runnable action, CompletableFuture<Void> result) {
        try {
            scheduler.accept(() -> {
                try {
                    action.run();
                } catch (Throwable error) {
                    result.completeExceptionally(error);
                }
            });
        } catch (Throwable error) {
            result.completeExceptionally(error);
        }
    }

    public static void syncAtEntity(Entity entity, Runnable runnable) {
        getFolia().runAtEntity(entity, task -> runnable.run());
    }

    public static WrappedTask asyncLater(Runnable runnable, long ticks) {
        return getFolia().runLaterAsync(runnable, ticks);
    }
    public static WrappedTask syncLater(Runnable runnable, long ticks) {
        return getFolia().runLater(runnable, ticks);
    }

    private static ServerImplementation getFolia() {
        return BetterRTP.getInstance().getFoliaHandler().get();
    }
}
