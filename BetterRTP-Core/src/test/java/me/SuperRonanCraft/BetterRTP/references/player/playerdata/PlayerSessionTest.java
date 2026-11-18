package me.SuperRonanCraft.BetterRTP.references.player.playerdata;

import me.SuperRonanCraft.BetterRTP.references.database.DatabasePlayers;
import org.bukkit.entity.Player;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.UUID;
import static org.junit.Assert.*;

public class PlayerSessionTest {
    private Player player(UUID id) {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getUniqueId")) return id;
                    if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                    if (method.getName().equals("equals")) return proxy == args[0];
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    @Test public void deferredLookupAndLoadCannotRestoreDepartedSession() {
        PlayerDataManager manager = new PlayerDataManager();
        Player player = player(UUID.randomUUID());
        PlayerData session = manager.startSession(player);
        manager.clear(player);
        assertNull(manager.getData(player));
        manager.applyIfCurrent(session, data -> fail("Departed session was updated"));
        assertNull(manager.getData(player.getUniqueId()));
    }

    @Test public void oldCallbacksAndQuitCannotAffectReconnect() {
        PlayerDataManager manager = new PlayerDataManager();
        UUID id = UUID.randomUUID();
        Player oldPlayer = player(id), newPlayer = player(id);
        PlayerData old = manager.startSession(oldPlayer);
        manager.clear(oldPlayer);
        PlayerData current = manager.startSession(newPlayer);
        manager.applyIfCurrent(old, data -> data.setRtpCount(99));
        manager.clear(oldPlayer);
        assertNull(manager.getData(oldPlayer));
        assertSame(current, manager.getData(newPlayer));
        assertSame(newPlayer, current.player);
        assertEquals(0, current.getRtpCount());
    }

    @Test public void reloadInvalidatesOldLoadEvenForSamePlayerObject() {
        PlayerDataManager manager = new PlayerDataManager();
        Player player = player(UUID.randomUUID());
        PlayerData old = manager.startSession(player);
        manager.clear();
        PlayerData current = manager.startSession(player);
        manager.applyIfCurrent(old, data -> fail("Old reload generation was updated"));
        manager.applyIfCurrent(current, data -> data.setRtpCount(7));
        assertEquals(7, current.getRtpCount());
    }

    @Test public void pendingSaveKeepsCapturedValuesAfterMutationAndQuit() {
        PlayerDataManager manager = new PlayerDataManager();
        Player player = player(UUID.randomUUID());
        PlayerData data = manager.startSession(player);
        data.setRtpCount(12);
        data.setGlobalCooldown(123456L);
        DatabasePlayers.Snapshot pending = DatabasePlayers.Snapshot.capture(data);
        data.setRtpCount(0);
        data.setGlobalCooldown(0);
        manager.clear(player);
        assertEquals(player.getUniqueId(), pending.uuid);
        assertEquals(12, pending.count);
        assertEquals(123456L, pending.cooldown);
        assertNull(manager.getData(player));
    }

    @Test public void deferredDatabaseWriterUsesSnapshotAfterQuit() throws Exception {
        PlayerDataManager manager = new PlayerDataManager();
        Player player = player(UUID.randomUUID());
        PlayerData data = manager.startSession(player);
        data.setRtpCount(23);
        data.setGlobalCooldown(987654L);
        java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.atomic.AtomicReference<java.util.List<Object>> written =
                new java.util.concurrent.atomic.AtomicReference<>();
        DatabasePlayers database = new DatabasePlayers() {
            @Override protected boolean sqlUpdate(String sql, java.util.List<Object> params) {
                written.set(params);
                return true;
            }
        };
        // Table setup without starting Bukkit or opening a database connection.
        java.lang.reflect.Field tables = database.getClass().getSuperclass().getSuperclass().getDeclaredField("tables");
        tables.setAccessible(true);
        tables.set(database, java.util.Collections.singletonList("Players"));
        java.util.concurrent.ExecutorService executor =
                me.SuperRonanCraft.BetterRTP.references.database.SQLiteExecutor.EXECUTOR;
        executor.submit(() -> {
            try { release.await(5, java.util.concurrent.TimeUnit.SECONDS); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        try {
            database.setData(data);
            data.setRtpCount(0);
            data.setGlobalCooldown(0);
            manager.clear(player);
        } finally {
            release.countDown();
        }
        executor.submit(() -> {}).get(5, java.util.concurrent.TimeUnit.SECONDS);
        assertEquals(java.util.Arrays.asList(player.getUniqueId().toString(), 23, 987654L), written.get());
        assertNull(manager.getData(player));
    }

    @Test public void lookupNeverCreatesSessionAndAcceptsNull() {
        PlayerDataManager manager = new PlayerDataManager();
        assertNull(manager.getData((Player) null));
        assertNull(manager.getData(player(UUID.randomUUID())));
    }
}
