package me.SuperRonanCraft.BetterRTP.references.player.playerdata;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    public PlayerData getData(@NonNull Player p) {
        return playerData.computeIfAbsent(p.getUniqueId(), uuid -> new PlayerData(p));
    }

    @Nullable
    public PlayerData getData(UUID id) {
        return playerData.get(id);
    }

    public void clear() {
        playerData.clear();
    }

    public void clear(Player p) {
        playerData.remove(p.getUniqueId());
    }
}
