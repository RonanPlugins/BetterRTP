package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class RTPCooldown {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>(); //Cooldown timer for each player
    private HashMap<UUID, Integer> locked = null; //Players locked from rtp'ing ever again
    public boolean enabled;
    private int
            timer, //Cooldown timer
            lockedAfter; //Rtp's before being locked

    public void load() {
        cooldowns.clear();
        if (locked != null)
            locked.clear();
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        enabled = config.getBoolean("Settings.Cooldown.Enabled");
        if (enabled) {
            timer = config.getInt("Settings.Cooldown.Time");
            lockedAfter = config.getInt("Settings.Cooldown.LockAfter");
            if (lockedAfter > 0)
                locked = new HashMap<>();
            loadFile();
        }
    }

    public void add(UUID id) {
        if (!enabled) return;
        cooldowns.put(id, System.currentTimeMillis());
        if (lockedAfter > 0) {
            if (locked.containsKey(id))
                locked.put(id, locked.get(id) + 1);
            else
                locked.put(id, 1);
            savePlayer(id, true, cooldowns.get(id), locked.get(id));
        } else
            savePlayer(id, true, cooldowns.get(id), 0);
    }

    public boolean exists(UUID id) {
        return cooldowns.containsKey(id);
    }

    public long timeLeft(UUID id) {
        long cooldown = cooldowns.getOrDefault(id, 0L);
        return ((cooldown / 1000) + timer) - (System.currentTimeMillis() / 1000);
    }

    public boolean locked(UUID id) {
        if (locked != null && locked.containsKey(id))
            return locked.get(id) >= lockedAfter;
        return false;
    }

    public void remove(UUID id) {
        if (!enabled) return;
        if (lockedAfter > 0) {
            locked.put(id, locked.getOrDefault(id, 1) - 1);
            if (locked.get(id) <= 0) { //Remove from file as well
                savePlayer(id, false, 0L, 0);
            } else { //Keep the player cached
                savePlayer(id, false, cooldowns.get(id), locked.get(id));
            }
            cooldowns.remove(id);
        } else { //Remove completely
            cooldowns.remove(id);
            savePlayer(id, false, 0L, 0);
        }
    }

    private void savePlayer(UUID id, boolean adding, long time, int attempts) {
        YamlConfiguration config = getFile();
        if (config == null) return;
        if (adding) { //Add player to file
            config.set(id.toString() + ".Time", time);
            if (attempts > 0)
                config.set(id.toString() + ".Attempts", attempts);
        } else { //Remove player from the file
            config.set(id.toString(), null);
        }
        try {
            config.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private YamlConfiguration config;
    private File configfile;

    private YamlConfiguration getFile() {
        if (config != null) {
            return config;
        } else {
            if (!configfile.exists()) {
                try {
                    configfile.getParentFile().mkdir();
                    configfile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                config = new YamlConfiguration();
                config.load(configfile);
                return config;
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void loadFile() {
        config = null;
        configfile = new File(BetterRTP.getInstance().getDataFolder(), "data/cooldowns.yml");
        YamlConfiguration config = getFile();
        if (config != null)
            for (String id : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(id);
                Long time = config.getLong(id + ".Time");
                cooldowns.put(uuid, time);
                if (lockedAfter > 0) {
                    int attempts = config.getInt(id + ".Attempts");
                    locked.put(uuid, attempts);
                }
            } catch (IllegalArgumentException e) {
                BetterRTP.getInstance().getLogger().info("UUID of `" + id + "` is invalid, please delete this!");
                //Bad uuid
            }
        }
    }
}
