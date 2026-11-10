package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.Getter;
import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueGenerator;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class DatabaseQueue extends SQLite {

    public DatabaseQueue() {
        super(DATABASE_TYPE.QUEUE);
    }

    @Override
    public List<String> getTables() {
        List<String> list = new ArrayList<>();
        list.add("Queue");
        return list;
    }

    public enum COLUMNS {
        ID("id", "integer PRIMARY KEY AUTOINCREMENT"),
        //Location Data
        X("x", "long"),
        Z("z", "long"),
        WORLD("world", "varchar(32)"),
        GENERATED("generated", "long")
        ;

        public final String name;
        public final String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    @Override public void load() {
        if (QueueHandler.isEnabled())
            super.load();
    }

    public List<QueueData> getInRange(QueueRangeData range) {
        final List<QueueData> queueDataList = new ArrayList<>();
        try {
            SQLiteExecutor.EXECUTOR.submit(() -> {
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    conn = getSQLConnection();
                    ps = conn.prepareStatement("SELECT * FROM " + tables.get(0) + " WHERE "
                            + COLUMNS.WORLD.name + " = '" + range.getWorld().getName() + "' AND "
                            + COLUMNS.X.name + " BETWEEN " + range.getXLow() + " AND " + range.getXHigh()
                            + " AND " + COLUMNS.Z.name + " BETWEEN " + range.getZLow() + " AND " + range.getZHigh()
                            + " ORDER BY RANDOM() LIMIT " + (QueueGenerator.queueMax + 1)
                    );
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        long x = rs.getLong(COLUMNS.X.name);
                        long z = rs.getLong(COLUMNS.Z.name);
                        String worldName = rs.getString(COLUMNS.WORLD.name);
                        int id = rs.getInt(COLUMNS.ID.name);
                        long generated = rs.getLong(COLUMNS.GENERATED.name);
                        World world = Bukkit.getWorld(worldName);
                        if (world != null) {
                            queueDataList.add(new QueueData(new Location(world, x, 69, z), generated, id));
                        }
                    }
                } catch (SQLException ex) {
                    BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
                } finally {
                    close(ps, rs, conn);
                }
            }).get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return queueDataList;
    }

    //Set a queue to save
    public QueueData addQueue(Location loc) {
        try {
            return SQLiteExecutor.EXECUTOR.submit(() -> {
                String sql = "INSERT INTO " + tables.get(0) + " ("
                        + COLUMNS.X.name + ", "
                        + COLUMNS.Z.name + ", "
                        + COLUMNS.WORLD.name + ", "
                        + COLUMNS.GENERATED.name + ") VALUES(?, ?, ?, ?)";
                List<Object> params = new ArrayList<Object>() {{
                    add(loc.getBlockX());
                    add(loc.getBlockZ());
                    add(loc.getWorld().getName());
                    add(System.currentTimeMillis());
                }};
                int database_id = createQueue(sql, params);
                return database_id >= 0 ? new QueueData(loc, System.currentTimeMillis(), database_id) : null;
            }).get();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private int createQueue(String statement, @NonNull List<Object> params) {
        Connection conn = null;
        PreparedStatement ps = null;
        int id = -1;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            Iterator<Object> it = params.iterator();
            int paramIndex = 1;
            while (it.hasNext()) {
                ps.setObject(paramIndex, it.next());
                paramIndex++;
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, null, conn);
        }
        return id;
    }

    public boolean removeLocation(Location loc) {
        try {
            return SQLiteExecutor.EXECUTOR.submit(() -> {
                String sql = "DELETE FROM " + tables.get(0) + " WHERE "
                        + COLUMNS.X.name + " = ? AND "
                        + COLUMNS.Z.name + " = ? AND "
                        + COLUMNS.WORLD.name + " = ?";
                List<Object> params = new ArrayList<Object>() {{
                    add(loc.getBlockX());
                    add(loc.getBlockZ());
                    add(loc.getWorld().getName());
                }};
                return sqlUpdate(sql, params);
            }).get();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Getter
    public static class QueueRangeData {

        int xLow, xHigh;
        int zLow, zHigh;
        World world;

        public QueueRangeData(RTPWorld rtpWorld) {
            this.xLow = rtpWorld.getCenterX() - rtpWorld.getMaxRadius();
            this.xHigh = rtpWorld.getCenterX() + rtpWorld.getMaxRadius();
            this.zLow = rtpWorld.getCenterZ() - rtpWorld.getMaxRadius();
            this.zHigh = rtpWorld.getCenterZ() + rtpWorld.getMaxRadius();
            this.world = rtpWorld.getWorld();
        }
    }
}
