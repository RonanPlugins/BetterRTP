package me.SuperRonanCraft.BetterRTP.player.rtp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/** Owns active requests. Identity checks prevent old callbacks from ending a newer request. */
public final class RTPOperationTracker {
    private final Map<UUID, Operation> active = new HashMap<>();

    public synchronized Operation start(UUID uuid, Object session) {
        if (active.containsKey(uuid)) return null;
        Operation operation = new Operation(uuid, session);
        active.put(uuid, operation);
        return operation;
    }

    public synchronized boolean contains(UUID uuid) { return active.containsKey(uuid); }

    public void cancel(UUID uuid, Object session) {
        Operation operation;
        synchronized (this) { operation = active.get(uuid); }
        if (operation != null && operation.session == session) operation.finish();
    }

    public void clear() {
        List<Operation> operations;
        synchronized (this) {
            operations = new ArrayList<>(active.values());
            active.clear();
        }
        RuntimeException failure = null;
        for (Operation operation : operations) {
            try { operation.finish(); } catch (RuntimeException ex) { failure = ex; }
        }
        if (failure != null) throw failure;
    }

    public final class Operation {
        private final UUID uuid;
        private final Object session;
        private final List<Runnable> cleanup = new ArrayList<>();
        private boolean finished;

        private Operation(UUID uuid, Object session) { this.uuid = uuid; this.session = session; }

        public boolean isActive() {
            synchronized (RTPOperationTracker.this) { return active.get(uuid) == this; }
        }

        public void onFinish(Runnable action) {
            synchronized (this) {
                if (!finished) { cleanup.add(action); return; }
            }
            action.run();
        }

        public void finish() {
            List<Runnable> actions;
            synchronized (this) {
                if (finished) return;
                finished = true;
                actions = new ArrayList<>(cleanup);
                cleanup.clear();
            }
            synchronized (RTPOperationTracker.this) { active.remove(uuid, this); }
            RuntimeException failure = null;
            for (Runnable action : actions) {
                try { action.run(); } catch (RuntimeException ex) { failure = ex; }
            }
            if (failure != null) throw failure;
        }

        /** Terminal future: cleanup also runs when result handling throws. */
        public void complete(CompletableFuture<Boolean> future, Runnable success, Runnable failure,
                             Consumer<Throwable> errors) {
            future.whenComplete((result, error) -> {
                try {
                    if (!isActive()) return;
                    if (error != null) errors.accept(error);
                    else if (Boolean.TRUE.equals(result)) success.run();
                    else failure.run();
                } catch (Throwable ex) {
                    errors.accept(ex);
                } finally {
                    finish();
                }
            });
        }
    }
}
