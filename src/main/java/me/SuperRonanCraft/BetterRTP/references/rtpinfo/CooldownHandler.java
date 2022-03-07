package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseCooldowns;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CooldownHandler {

    @Getter boolean enabled, loaded;
    private int
            timer, //Cooldown timer
            lockedAfter; //Rtp's before being locked
    private final List<Player> downloading = new ArrayList<>();

    public void load() {
        //configfile = new File(BetterRTP.getInstance().getDataFolder(), "data/cooldowns.yml");
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        enabled = config.getBoolean("Settings.Cooldown.Enabled");
        downloading.clear();
        loaded = false;
        if (enabled) {
            timer = config.getInt("Settings.Cooldown.Time");
            lockedAfter = config.getInt("Settings.Cooldown.LockAfter");
        }
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            getDatabase().load();
            checkLater();
        });
    }

    private void checkLater() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {
            if (getDatabase().isLoaded()) {
                OldCooldownConverter.loadOldCooldowns();
                //Load any online players cooldowns (mostly after a reload)
                for (Player p : Bukkit.getOnlinePlayers())
                    loadPlayer(p);
                loaded = true;
            } else
                checkLater();
        }, 10L);
    }

    public void add(Player player) {
        if (!enabled) return;
        CooldownData data = getData(player).getCooldown();
        if (data == null)
            data = new CooldownData(player.getUniqueId(), 0L, 0);
        if (lockedAfter > 0)
            data.setUses(data.getUses() + 1);
        data.setTime(System.currentTimeMillis());
        getData(player).setCooldown(data);
        savePlayer(data, false);
    }

    public boolean exists(Player p) {
        return getData(p).getCooldown() != null;
    }

    @Nullable
    public CooldownData getPlayer(Player p) {
        return getData(p).getCooldown();
    }

    public long timeLeft(CooldownData data) {
        long cooldown = data.getTime();
        return ((cooldown / 1000) + timer) - (System.currentTimeMillis() / 1000);
    }

    public boolean locked(CooldownData data) {
        return lockedAfter > 0 && data.uses >= lockedAfter;
    }

    public void removeCooldown(Player player) {
        if (!enabled) return;
        CooldownData data = getData(player).getCooldown();
        if (data != null)
            if (lockedAfter > 0) {
                //uses.put(id, uses.getOrDefault(id, 1) - 1);
                if (data.getUses() <= 0) { //Remove from file as well
                    savePlayer(data, true);
                    getData(player).setCooldown(null);
                } else { //Keep the player cached
                    savePlayer(data, false);
                }
            } else { //Remove completely
                getData(player).setCooldown(null);
                savePlayer(data, true);
            }
    }

    private void savePlayer(CooldownData data, boolean remove) {
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
                if (!remove) {
                    getDatabase().setCooldown(data);
                } else {
                    getDatabase().removePlayer(data.getUuid());
                }
            });
    }

    public void loadPlayer(Player player) {
        downloading.add(player);
        if (isEnabled()) {
            CooldownData cooldown = getDatabase().getCooldown(player.getUniqueId());
            if (cooldown != null)
                getData(player).setCooldown(cooldown);
        }
        downloading.remove(player);
    }

    public boolean loadedPlayer(Player player) {
        return !downloading.contains(player);
    }

    @Deprecated
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
                BetterRTP.getInstance().getDatabaseCooldowns().setCooldown(cooldownData);
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
        return BetterRTP.getInstance().getDatabaseCooldowns();
    }

    private PlayerData getData(Player p) {
        return HelperPlayer.getData(p);
    }
}
