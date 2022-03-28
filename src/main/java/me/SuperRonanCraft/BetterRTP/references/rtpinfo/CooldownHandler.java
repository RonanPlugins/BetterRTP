package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseCooldownsWorlds;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseCooldownsGlobal;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CooldownHandler {

    @Getter boolean enabled, loaded;
    private int
            timer, //Cooldown timer
            lockedAfter; //Rtp's before being locked
    private final List<Player> downloading = new ArrayList<>();
    private final DatabaseCooldownsWorlds cooldowns = new DatabaseCooldownsWorlds();
    private final DatabaseCooldownsGlobal globalCooldown = new DatabaseCooldownsGlobal();

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
            globalCooldown.load();
            cooldowns.load();
            checkLater();
        });
    }

    private void checkLater() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(BetterRTP.getInstance(), () -> {
            AtomicBoolean loaded = new AtomicBoolean(true);
            if (!globalCooldown.isLoaded()) {
                checkLater();
                return;
            } else if (!cooldowns.isLoaded()) {
               checkLater();
               return;
            }
            //OldCooldownConverter.loadOldCooldowns();
            //Load any online players cooldowns (mostly after a reload)
            for (Player p : Bukkit.getOnlinePlayers())
                loadPlayer(p);
        }, 10L);
    }

    public void add(Player player, World world) {
        if (!enabled) return;
        CooldownData data = getData(player).getCooldown();
        if (data == null)
            data = new CooldownData(player.getUniqueId(), 0L, 0, world);
        if (lockedAfter > 0)
            data.setUses(data.getUses() + 1);
        data.setTime(System.currentTimeMillis());
        getData(player).setCooldown(data);
        savePlayer(data, false);
    }

    public boolean exists(Player p) {
        return getData(p).getCooldowns() != null;
    }

    @Nullable
    public CooldownData get(Player p, World world) {
        List<CooldownData> data = getData(p).getCooldowns();
        if (data != null)
            for (CooldownData cd : data)
                if (cd.getWorld() == world)
                    return cd;
        return null;
    }

    @Nullable
    public CooldownData getGlobal(Player p) {
        return globalCooldown.getCooldown(p.getUniqueId());
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
                    getDatabase(data.getWorld()).setCooldown(data);
                } else {
                    getDatabase(data.getWorld()).removePlayer(data.getUuid(), data.getWorld());
                }
            });
    }

    public void loadPlayer(Player player) {
        if (!isEnabled()) return;
        downloading.add(player);
        List<CooldownData> cooldowns = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            CooldownData cooldown = getDatabaseWorlds().getCooldown(player.getUniqueId(), world);
            if (cooldown != null)
                getData(player).getCooldowns().add(cooldown);
        }
        downloading.remove(player);
    }

    public boolean loadedPlayer(Player player) {
        return !downloading.contains(player);
    }

    private DatabaseCooldownsWorlds getDatabaseWorlds() {
        return cooldowns;
    }

    private PlayerData getData(Player p) {
        return HelperPlayer.getData(p);
    }
}

//Old yaml file based system, no longer useful as of 3.3.1
/*@Deprecated
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
    }*/
