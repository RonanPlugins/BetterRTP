package me.SuperRonanCraft.BetterRTP.references.player.playerdata;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    /** Called only by join/enable/reload lifecycle handling. */
    public PlayerData startSession(@NonNull Player p) {
        PlayerData data = new PlayerData(p);
        playerData.put(p.getUniqueId(), data);
        return data;
    }

    /** Lookup only: a departed or replaced session must never be recreated. */
    @Nullable
    public PlayerData getData(Player p) {
        if (p == null) return null;
        PlayerData data = playerData.get(p.getUniqueId());
        return data != null && data.player == p ? data : null;
    }

    public void applyIfCurrent(PlayerData session, Consumer<PlayerData> update) {
        playerData.computeIfPresent(session.player.getUniqueId(), (id, current) -> {
            if (current == session) update.accept(current);
            return current;
        });
    }

    @Nullable
    public PlayerData getData(UUID id) {
        return playerData.get(id);
    }

    public void clear() {
        playerData.clear();
    }

    public void clear(Player p) {
        playerData.computeIfPresent(p.getUniqueId(), (id, data) -> data.player == p ? null : data);
    }
}
