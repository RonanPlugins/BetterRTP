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
        PlayerData data = playerData.get(p.getUniqueId());
        if (data != null && data.player == p)
            return data;
        // Don't cache data for a player that already left (e.g. a placeholder or damage
        // event fired after quit), it would keep the offline Player in memory forever
        if (!p.isOnline())
            return data != null ? data : new PlayerData(p);
        data = new PlayerData(p);
        playerData.put(p.getUniqueId(), data);
        return data;
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
