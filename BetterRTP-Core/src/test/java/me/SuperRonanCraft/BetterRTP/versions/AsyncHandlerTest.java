package me.SuperRonanCraft.BetterRTP.versions;

import org.bukkit.Chunk;
import org.junit.Test;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;

public class AsyncHandlerTest {
    @Test public void synchronousFallbackLoadsOnlyOnRegionAndReschedulesInspection() {
        Queue<Runnable> region = new ArrayDeque<>();
        AtomicBoolean loaded = new AtomicBoolean();
        AtomicBoolean checked = new AtomicBoolean();
        CompletableFuture<Void> result = AsyncHandler.atLoadedChunk(region::add, () -> {
            loaded.set(true);
            return CompletableFuture.completedFuture(null);
        }, () -> checked.set(true));
        assertFalse(loaded.get());
        region.remove().run();
        assertTrue(loaded.get());
        assertFalse(checked.get());
        assertFalse(result.isDone());
        region.remove().run();
        assertTrue(checked.get());
        result.join();
    }

    @Test public void asynchronousCompletionDoesNotInspectBlocksOnCompletingThread() {
        Queue<Runnable> region = new ArrayDeque<>();
        CompletableFuture<Chunk> chunk = new CompletableFuture<>();
        AtomicBoolean checked = new AtomicBoolean();
        CompletableFuture<Void> result = AsyncHandler.atLoadedChunk(region::add, () -> chunk,
                () -> checked.set(true));
        region.remove().run();
        assertTrue(region.isEmpty());
        chunk.complete(null);
        assertFalse(checked.get());
        region.remove().run();
        assertTrue(checked.get());
        result.join();
    }

    @Test public void synchronousLoadFailureIsReported() {
        RuntimeException failure = new RuntimeException("load failed");
        assertFailure(failure, AsyncHandler.atLoadedChunk(Runnable::run, () -> { throw failure; },
                () -> fail("Must not inspect failed chunk")));
    }

    @Test public void asynchronousLoadFailureIsReported() {
        RuntimeException failure = new RuntimeException("load failed");
        CompletableFuture<Chunk> chunk = new CompletableFuture<>();
        CompletableFuture<Void> result = AsyncHandler.atLoadedChunk(Runnable::run, () -> chunk,
                () -> fail("Must not inspect failed chunk"));
        chunk.completeExceptionally(failure);
        assertFailure(failure, result);
    }

    @Test public void inspectionFailureIsReported() {
        RuntimeException failure = new RuntimeException("inspection failed");
        assertFailure(failure, AsyncHandler.atLoadedChunk(Runnable::run,
                () -> CompletableFuture.completedFuture(null), () -> { throw failure; }));
    }

    @Test public void schedulingFailureIsReported() {
        RuntimeException failure = new RuntimeException("scheduling failed");
        assertFailure(failure, AsyncHandler.atLoadedChunk(task -> { throw failure; },
                () -> { fail("Must not load without region ownership"); return null; }, () -> {}));
    }

    private static void assertFailure(Throwable expected, CompletableFuture<Void> result) {
        try {
            result.join();
            fail("Expected failure");
        } catch (CompletionException failure) {
            assertSame(expected, failure.getCause());
        }
    }
}
