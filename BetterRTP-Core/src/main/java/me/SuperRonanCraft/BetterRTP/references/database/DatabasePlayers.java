package me.SuperRonanCraft.BetterRTP.references.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;

public class DatabasePlayers extends SQLite {

    public DatabasePlayers() {
        super(DATABASE_TYPE.PLAYERS);
    }

    @Override
    public List<String> getTables() {
        List<String> list = new ArrayList<>();
        list.add("Players");
        return list;
    }

    public enum COLUMNS {
        UUID("uuid", "varchar(32) PRIMARY KEY"),
        //COOLDOWN DATA
        COUNT("count", "long"),
        LAST_COOLDOWN_DATE("last_rtp_date", "long"),
        ;

        public final String name;
        public final String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    /** Immutable persistence values; never retains a Bukkit player/session. */
    public static final class Snapshot {
        public final UUID uuid;
        public final int count;
        public final long cooldown;

        public Snapshot(UUID uuid, int count, long cooldown) {
            this.uuid = uuid;
            this.count = count;
            this.cooldown = cooldown;
        }

        public static Snapshot capture(PlayerData data) {
            return new Snapshot(data.player.getUniqueId(), data.getRtpCount(), data.getGlobalCooldown());
        }
    }

    /** Existing API: load into this session only while it is still current. */
    public void setupData(PlayerData data) {
        if (data == null) return;
        Snapshot stored = loadData(data.player.getUniqueId());
        if (stored != null)
            BetterRTP.getInstance().getPlayerDataManager().applyIfCurrent(data, current -> {
                current.setRtpCount(stored.count);
                current.setGlobalCooldown(stored.cooldown);
            });
    }

    public Snapshot loadData(UUID uuid) {
        try {
            return SQLiteExecutor.EXECUTOR.submit(() -> {
                try (Connection conn = getSQLConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tables.get(0) + " WHERE " + COLUMNS.UUID.name + " = ?")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next())
                            return new Snapshot(uuid, Math.toIntExact(rs.getLong(COLUMNS.COUNT.name)),
                                    rs.getLong(COLUMNS.LAST_COOLDOWN_DATE.name));
                    }
                } catch (SQLException ex) {
                    BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
                }
                return null;
            }).get();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** Compatibility entry point: capture before deferring database work. */
    public void setData(PlayerData data) {
        if (data != null) setData(Snapshot.capture(data));
    }

    public void setData(Snapshot data) {
        SQLiteExecutor.EXECUTOR.submit(() -> {
            String sql = "INSERT OR REPLACE INTO " + tables.get(0) + " ("
                    + COLUMNS.UUID.name + ", " + COLUMNS.COUNT.name + ", "
                    + COLUMNS.LAST_COOLDOWN_DATE.name + ") VALUES(?, ?, ?)";
            List<Object> params = java.util.Arrays.asList(data.uuid.toString(), data.count, data.cooldown);
            sqlUpdate(sql, params);
        });
    }
}
