package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;

import java.util.HashMap;
import java.util.UUID;

public class RTPCooldown {

    public HashMap<UUID, Long> cooldowns = new HashMap<>();
    public boolean enabled;
    private int timer;

    public void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        enabled = config.getBoolean("Settings.Cooldown.Enabled");
        timer = config.getInt("Settings.Cooldown.Time");
    }

    public void add(UUID id) {
        cooldowns.put(id, System.currentTimeMillis());
    }

    public boolean exists(UUID id) {
        return cooldowns.containsKey(id);
    }

    public long timeLeft(UUID id) {
        return ((cooldowns.getOrDefault(id, (long) 0) / 1000) + timer) - (System.currentTimeMillis() / 1000);
    }

    public void remove(UUID id) {
        cooldowns.remove(id);
    }
}
