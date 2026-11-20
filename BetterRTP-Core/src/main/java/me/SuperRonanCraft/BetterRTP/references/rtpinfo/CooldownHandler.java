package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseCooldowns;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.database.DatabasePlayers;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;

public class CooldownHandler {

    @Getter boolean enabled, loaded, cooldownByWorld;
    @Getter private int defaultCooldownTime; //Global Cooldown timer
    private int lockedAfter; //Rtp's before being locked

    public void load() {
        FileOther.FILETYPE config = FileOther.FILETYPE.CONFIG;
        enabled = config.getBoolean("Settings.Cooldown.Enabled");

        loaded = false;
        if (enabled) {
            defaultCooldownTime = config.getInt("Settings.Cooldown.Time");
            BetterRTP.debug("Cooldown = " + defaultCooldownTime);
            lockedAfter = config.getInt("Settings.Cooldown.LockAfter");
            cooldownByWorld = config.getBoolean("Settings.Cooldown.PerWorld");
        }
        queueDownload();
    }

    private void queueDownload() {
        AsyncHandler.asyncLater(() -> {
            if (cooldownByWorld && !DatabaseHandler.getCooldowns().isLoaded()) {
               queueDownload();
               return;
            }
            if (!DatabaseHandler.getPlayers().isLoaded()) {
               queueDownload();
               return;
            }
            //Load any online players cooldowns (mostly after a reload)
            for (Player p : Bukkit.getOnlinePlayers())
                loadPlayer(p);
            loaded = true;
        }, 10L);
    }

    public void add(Player player, World world) {
        if (!enabled) return;
        PlayerData playerData = getData(player);
        if (playerData == null) return;
        if (cooldownByWorld) {
            Map<World, CooldownData> cooldowns = playerData.getCooldowns();
            CooldownData data = cooldowns.getOrDefault(world, new CooldownData(player.getUniqueId(), 0L));
            playerData.setRtpCount(playerData.getRtpCount() + 1);
            data.setTime(System.currentTimeMillis());
            playerData.setGlobalCooldown(data.getTime());
            cooldowns.put(world, data);
            savePlayer(playerData, world, data, false);
        } else
            add(player);
    }

    private void add(Player player) {
        if (!enabled) return;
        PlayerData playerData = getData(player);
        if (playerData == null) return;
        playerData.setRtpCount(playerData.getRtpCount() + 1);
        playerData.setGlobalCooldown(System.currentTimeMillis());
        savePlayer(playerData, null, null, false);
    }

    @Nullable
    public CooldownData get(Player p, World world) {
        PlayerData data = getData(p);
        if (data == null) return null;
        if (cooldownByWorld) {
            Map<World, CooldownData> cooldownData = data.getCooldowns();
            return cooldownData.getOrDefault(world, null);
        } else if (data.getGlobalCooldown() > 0) {
            return new CooldownData(p.getUniqueId(), data.getGlobalCooldown());
        }
        return null;
    }

    public long timeLeft(CommandSender sendi, CooldownData data, WorldPlayer pWorld) {
        long cooldown = data.getTime();
        long timeLeft = ((cooldown / 1000) + pWorld.getCooldown()) - (System.currentTimeMillis() / 1000);
        return timeLeft * 1000L;
    }

    public boolean locked(Player player) {
        PlayerData data = getData(player);
        return lockedAfter > 0 && data != null && data.getRtpCount() >= lockedAfter;
    }

    public void removeCooldown(Player player, World world) {
        if (!enabled) return;
        PlayerData playerData = getData(player);
        if (playerData == null) return;
        CooldownData cooldownData = playerData.getCooldowns().getOrDefault(world, null);
        if (cooldownData != null) {
            if (lockedAfter > 0) {
                if (playerData.getRtpCount() <= 0) { //Remove from file as well
                    savePlayer(playerData, world, cooldownData, true);
                    playerData.getCooldowns().remove(world);
                } else { //Keep the player cached
                    savePlayer(playerData, world, cooldownData, false);
                }
            } else { //Remove completely
                playerData.getCooldowns().remove(world);
                savePlayer(playerData, world, cooldownData, true);
            }
        } else if (!cooldownByWorld) {
            playerData.setGlobalCooldown(0);
            savePlayer(playerData, null, null, true);
        }
    }

    private void savePlayer(PlayerData session, @Nullable World world, @Nullable CooldownData data, boolean remove) {
        DatabasePlayers.Snapshot snapshot = DatabasePlayers.Snapshot.capture(session);
        CooldownData cooldown = data == null ? null : new CooldownData(data.getUuid(), data.getTime());
        DatabaseCooldowns worlds = getDatabaseWorlds();
        // Submit player values now, preserving submission order and avoiding a later lookup.
        DatabaseHandler.getPlayers().setData(snapshot);
        if (world != null && cooldown != null && worlds != null) {
            AsyncHandler.async(() -> {
                if (remove) worlds.removePlayer(cooldown.getUuid(), world);
                else worlds.setCooldown(world, cooldown);
            });
        }
    }

    /** Capture the session before scheduling; completion cannot affect a reconnect. */
    public void loadPlayer(Player player) {
        if (!isEnabled()) return;
        PlayerData session = getData(player);
        if (session == null) return;
        synchronized (session) {
            if (session.loading || session.cooldownsLoaded) return;
            session.loading = true;
        }
        UUID uuid = player.getUniqueId();
        List<World> worlds = Bukkit.getWorlds();
        DatabaseCooldowns databaseWorlds = getDatabaseWorlds();
        AsyncHandler.async(() -> {
            try {
                if (getData(player) != session) return;
                Map<World, CooldownData> cooldowns = new HashMap<>();
                if (databaseWorlds != null) {
                    for (World world : worlds) {
                        CooldownData cooldown = databaseWorlds.getCooldown(uuid, world);
                        if (cooldown != null) cooldowns.put(world, cooldown);
                    }
                }
                DatabasePlayers.Snapshot stored = DatabaseHandler.getPlayers().loadData(uuid);
                BetterRTP.getInstance().getPlayerDataManager().applyIfCurrent(session, current -> {
                    current.getCooldowns().putAll(cooldowns);
                    if (stored != null) {
                        current.setRtpCount(stored.count);
                        current.setGlobalCooldown(stored.cooldown);
                    }
                    current.cooldownsLoaded = true;
                });
            } finally {
                session.loading = false;
            }
        });
    }

    public boolean loadedPlayer(Player player) {
        PlayerData data = getData(player);
        return data != null && !data.loading;
    }

    @Nullable
    private DatabaseCooldowns getDatabaseWorlds() {
        if (cooldownByWorld)
            return DatabaseHandler.getCooldowns();
        return null;
    }

    private PlayerData getData(Player p) {
        return HelperPlayer.getData(p);
    }
}
