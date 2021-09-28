package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.player.PlayerInfo;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseCooldowns;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CooldownHandler {

    //private final HashMap<UUID, Long> cooldowns = new HashMap<>(); //Cooldown timer for each player
    //private HashMap<UUID, Integer> uses = null; //Players locked from rtp'ing ever again
    @Getter boolean enabled;
    private int
            timer, //Cooldown timer
            lockedAfter; //Rtp's before being locked

    public void load() {
        //configfile = new File(BetterRTP.getInstance().getDataFolder(), "data/cooldowns.yml");
        getPInfo().getCooldown().clear();
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        enabled = config.getBoolean("Settings.Cooldown.Enabled");
        if (enabled) {
            timer = config.getInt("Settings.Cooldown.Time");
            lockedAfter = config.getInt("Settings.Cooldown.LockAfter");
            //if (lockedAfter > 0)
            //    uses = new HashMap<>();
        }
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            getDatabase().load();
            OldCooldownConverter.loadOldCooldowns();
            //Load any online players cooldowns (mostly after a reload)
            for (Player p : Bukkit.getOnlinePlayers())
                loadPlayer(p.getUniqueId());
        });
    }

    public void add(Player player) {
        if (!enabled) return;
        CooldownData data = getPInfo().getCooldown().getOrDefault(player,
                new CooldownData(player.getUniqueId(), System.currentTimeMillis(), 0));
        if (lockedAfter > 0)
            data.setUses(data.getUses() + 1);
        savePlayer(data);
    }

    public boolean exists(UUID id) {
        return cooldowns.containsKey(id);
    }

    public long timeLeft(UUID id) {
        long cooldown = cooldowns.getOrDefault(id, 0L);
        return ((cooldown / 1000) + timer) - (System.currentTimeMillis() / 1000);
    }

    public boolean locked(UUID id) {
        if (uses != null && uses.containsKey(id))
            return uses.get(id) >= lockedAfter;
        return false;
    }

    public void removeCooldown(Player player) {
        if (!enabled) return;
        CooldownData data = getPInfo().getCooldown().get(player);
        if (data != null)
            if (lockedAfter > 0) {
                //uses.put(id, uses.getOrDefault(id, 1) - 1);
                if (data.getUses() <= 0) { //Remove from file as well
                    savePlayer(data, true);
                    getPInfo().getCooldown().remove(player);
                } else { //Keep the player cached
                    savePlayer(data, false);
                }
            } else { //Remove completely
                getPInfo().getCooldown().remove(player);
                savePlayer(data, true);
            }
    }

    private void savePlayer(CooldownData data, boolean remove) {
        if (!remove) {
            getDatabase().setCooldown(data);
        } else {
            getDatabase().removePlayer(data.getUuid());
        }
        /*YamlConfiguration config = getFile();
        if (config == null) {
            BetterRTP.getInstance().getLogger().severe("Unabled to save cooldown for the players UUID " + id
                    + ". Cooldown file might have been compromised!");
            return;
        }
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
        }*/
    }

    /*private File configfile;

    /*private YamlConfiguration getFile() {
        if (!configfile.exists()) {
            try {
                configfile.getParentFile().mkdir();
                configfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(configfile);
            return config;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public void loadPlayer(UUID uuid) {
        if (isEnabled()) {
            List<Object> data = getDatabase().getCooldown(uuid);
            if (data != null) {
                cooldowns.put(uuid, (Long) data.get(0));
                uses.put(uuid, (int) data.get(1));
            }
            /*String id = uuid.toString();
            YamlConfiguration config = getFile();
            if (config != null && config.isConfigurationSection(id))
                try {
                    Long time = config.getLong(id + ".Time");
                    cooldowns.put(uuid, time);
                    if (lockedAfter > 0) {
                        int attempts = config.getInt(id + ".Attempts");
                        uses.put(uuid, attempts);
                    }
                } catch (IllegalArgumentException e) {
                    BetterRTP.getInstance().getLogger().info("UUID of `" + id + "` is invalid, please delete this!");
                    //Bad uuid
                }*/
        }
    }

    public void unloadPlayer(UUID uuid) {
        cooldowns.remove(uuid);
        if (uses != null)
            uses.remove(uuid);
    }

    static class OldCooldownConverter {

        static void loadOldCooldowns() {
            File file = new File(BetterRTP.getInstance().getDataFolder(), "data/cooldowns.yml");
            YamlConfiguration config = getFile(file);
            if (config == null) return;
            if (config.getBoolean("Converted")) return;
            List<CooldownData> cooldownData = new ArrayList<>();
            for (String id : config.getConfigurationSection("").getKeys(false)) {
                try {
                    Long time = config.getLong(id + ".Time");
                    UUID uuid = UUID.fromString(id);
                    int uses = config.getInt(id + ".Attempts");
                    cooldownData.add(new CooldownData(uuid, time, uses));
                } catch (IllegalArgumentException e) {
                    //Invalid UUID
                }
            }
            config.set("Converted", true);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BetterRTP.getInstance().getLogger().info("Cooldowns converting to new database...");
            Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
                BetterRTP.getInstance().getDatabase().setCooldown(cooldownData);
                BetterRTP.getInstance().getLogger().info("Cooldowns have been converted to the new database!");
            });
        }

        private static YamlConfiguration getFile(File configfile) {
            if (!configfile.exists()) {
                return null;
                /*try {
                    configfile.getParentFile().mkdir();
                    configfile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
            try {
                YamlConfiguration config = new YamlConfiguration();
                config.load(configfile);
                return config;
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private DatabaseCooldowns getDatabase() {
        return BetterRTP.getInstance().getDatabase();
    }

    private PlayerInfo getPInfo() {
        return BetterRTP.getInstance().getpInfo();
    }
}
