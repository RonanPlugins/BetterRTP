package me.SuperRonanCraft.BetterRTP.player.rtp;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

public class RTPOperationTest {
    private final RTPOperationTracker tracker = new RTPOperationTracker();
    private final UUID uuid = UUID.randomUUID();
    private final Object session = new Object();
    private final AtomicReference<Throwable> error = new AtomicReference<>();

    private RTPOperationTracker.Operation start() { return tracker.start(uuid, session); }
    private Runnable unexpected() { return () -> fail("Unexpected continuation"); }

    private RTPPlayer request(RTPOperationTracker.Operation operation) {
        Player player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("isOnline")) return true;
                    if (method.getName().equals("getUniqueId")) return uuid;
                    throw new UnsupportedOperationException(method.getName());
                });
        return new RTPPlayer(player, null, null, RTP_TYPE.JOIN, operation) {
            @Override void logError(Throwable failure) { error.set(failure); }
        };
    }

    @Test public void cancelledPreEventDoesNotStartInstantSearch() {
        RTPOperationTracker.Operation operation = start();
        request(operation).afterPreEvent(true, unexpected());
        assertNull(error.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void cancelledPreEventDisposesDelayAndDoesNotSchedule() {
        RTPOperationTracker.Operation operation = start();
        AtomicInteger disposed = new AtomicInteger();
        operation.onFinish(disposed::incrementAndGet);
        request(operation).afterPreEvent(true, unexpected());
        assertNull(error.get());
        operation.finish();
        assertEquals(1, disposed.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void exceptionalChunkFutureEndsOperation() {
        RTPOperationTracker.Operation operation = start();
        CompletableFuture<Chunk> future = new CompletableFuture<>();
        request(operation).awaitChunk(future, unexpected());
        RuntimeException failure = new RuntimeException("chunk load failed");
        future.completeExceptionally(failure);
        assertSame(failure, error.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void exceptionInChunkContinuationEndsOperation() {
        RTPOperationTracker.Operation operation = start();
        RuntimeException failure = new RuntimeException("location check failed");
        request(operation).awaitChunk(CompletableFuture.completedFuture(null), () -> { throw failure; });
        assertSame(failure, error.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void exceptionalTeleportFutureEndsOperation() {
        RTPOperationTracker.Operation operation = start();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        operation.complete(future, unexpected(), unexpected(), error::set);
        RuntimeException failure = new RuntimeException("teleport failed");
        future.completeExceptionally(failure);
        assertSame(failure, error.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void falseTeleportRunsFailureOnly() {
        AtomicInteger failures = new AtomicInteger();
        start().complete(CompletableFuture.completedFuture(false), unexpected(), failures::incrementAndGet, error::set);
        assertEquals(1, failures.get());
        assertNull(error.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void exceptionInPostTeleportEffectsStillCleansUp() {
        RuntimeException failure = new RuntimeException("effect failed");
        start().complete(CompletableFuture.completedFuture(true), () -> { throw failure; }, unexpected(), error::set);
        assertSame(failure, error.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void successRunsOnceAndReleasesOperation() {
        AtomicInteger successes = new AtomicInteger();
        start().complete(CompletableFuture.completedFuture(true), successes::incrementAndGet, unexpected(), error::set);
        assertEquals(1, successes.get());
        assertFalse(tracker.contains(uuid));
    }

    @Test public void quitAndStaleCompletionCannotRemoveNewOperation() {
        RTPOperationTracker.Operation old = start();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        old.complete(future, unexpected(), unexpected(), error::set);
        tracker.cancel(uuid, session);
        RTPOperationTracker.Operation current = tracker.start(uuid, new Object());
        tracker.cancel(uuid, session);
        future.complete(true);
        old.finish();
        assertTrue(current.isActive());
        assertNull(error.get());
    }

    @Test public void reloadClearsAllAndRunsEachCleanupOnce() {
        RTPOperationTracker.Operation first = start();
        RTPOperationTracker.Operation second = tracker.start(UUID.randomUUID(), new Object());
        AtomicInteger disposed = new AtomicInteger();
        first.onFinish(disposed::incrementAndGet);
        second.onFinish(disposed::incrementAndGet);
        tracker.clear();
        tracker.clear();
        assertEquals(2, disposed.get());
        assertFalse(first.isActive());
        assertFalse(second.isActive());
        first.onFinish(disposed::incrementAndGet);
        assertEquals(3, disposed.get());
    }

    @Test public void cleanupFailureDoesNotRetainOtherOperations() {
        RTPOperationTracker.Operation first = start();
        RTPOperationTracker.Operation second = tracker.start(UUID.randomUUID(), new Object());
        AtomicInteger disposed = new AtomicInteger();
        first.onFinish(() -> { throw new IllegalStateException("task cancellation failed"); });
        first.onFinish(disposed::incrementAndGet);
        second.onFinish(disposed::incrementAndGet);
        try { tracker.clear(); fail("Expected cleanup failure"); } catch (IllegalStateException expected) { }
        assertEquals(2, disposed.get());
        assertFalse(first.isActive());
        assertFalse(second.isActive());
    }

    @Test public void duplicateRequestCannotReplaceActiveRequest() {
        RTPOperationTracker.Operation first = start();
        assertNull(start());
        assertTrue(first.isActive());
    }
    @Test public void lifecycleCleanupUnregistersListenerEvenWhenTaskCancelThrows() {
        org.bukkit.event.HandlerList handlers = new org.bukkit.event.HandlerList();
        org.bukkit.event.Listener listener = new org.bukkit.event.Listener() {};
        org.bukkit.plugin.Plugin plugin = (org.bukkit.plugin.Plugin) Proxy.newProxyInstance(
                org.bukkit.plugin.Plugin.class.getClassLoader(), new Class<?>[]{org.bukkit.plugin.Plugin.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("isEnabled")) return true;
                    throw new UnsupportedOperationException(method.getName());
                });
        handlers.register(new org.bukkit.plugin.RegisteredListener(listener, (target, event) -> {},
                org.bukkit.event.EventPriority.NORMAL, plugin, false));
        AtomicInteger cancelled = new AtomicInteger();
        com.tcoded.folialib.wrapper.task.WrappedTask task = new com.tcoded.folialib.wrapper.task.WrappedTask() {
            public void cancel() { cancelled.incrementAndGet(); throw new IllegalStateException("cancel failed"); }
            public boolean isCancelled() { return cancelled.get() > 0; }
            public org.bukkit.plugin.Plugin getOwningPlugin() { return plugin; }
            public boolean isAsync() { return false; }
        };
        RTPOperationTracker.Operation operation = start();
        operation.onFinish(() -> RTPDelay.dispose(task, listener));
        try {
            assertEquals(1, handlers.getRegisteredListeners().length);
            try { tracker.clear(); fail("Expected task cancellation failure"); }
            catch (IllegalStateException expected) { }
            assertEquals(0, handlers.getRegisteredListeners().length);
            assertEquals(1, cancelled.get());
            assertFalse(tracker.contains(uuid));
        } finally {
            handlers.unregister(listener);
        }
    }

}
