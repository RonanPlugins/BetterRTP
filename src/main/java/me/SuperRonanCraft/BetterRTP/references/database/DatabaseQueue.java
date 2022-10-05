package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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

    public List<QueueData> getQueues() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<QueueData> queueDataList = new ArrayList<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tables.get(0));

            rs = ps.executeQuery();
            while (rs.next()) {
                long x = rs.getLong(COLUMNS.X.name);
                long z = rs.getLong(COLUMNS.Z.name);
                String worldName = rs.getString(COLUMNS.WORLD.name);
                int id = rs.getInt(COLUMNS.ID.name);
                long generated = rs.getLong(COLUMNS.GENERATED.name);
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    queueDataList.add(new QueueData(new Location(world, x, 69 /*giggity*/, z), generated, id));
                    //queueDataList.add(data);
                }
            }
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return queueDataList;
    }

    //Set a queue to save
    public QueueData addQueue(Location loc) {
        String pre = "INSERT INTO ";
        String sql = pre + tables.get(0) + " ("
                + COLUMNS.X.name + ", "
                + COLUMNS.Z.name + ", "
                + COLUMNS.WORLD.name + ", "
                + COLUMNS.GENERATED.name + " "
                //+ COLUMNS.USES.name + " "
                + ") VALUES(?, ?, ?, ?)";
        List<Object> params = new ArrayList<Object>() {{
                add(loc.getBlockX());
                add(loc.getBlockZ());
                add(loc.getWorld().getName());
                add(System.currentTimeMillis());
                //add(data.getUses());
        }};
        //return sqlUpdate(sql, params);
        int database_id = createQueue(sql, params);
        if (database_id >= 0)
            return new QueueData(loc, System.currentTimeMillis(), database_id);
        return null;
    }

    public int getCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tables.get(0));

            rs = ps.executeQuery();
            count = rs.getFetchSize();
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return count;
    }

    private int createQueue(String statement, @NonNull List<Object> params) {
        Connection conn = null;
        PreparedStatement ps = null;
        int id = -1;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);
            Iterator<Object> it = params.iterator();
            int paramIndex = 1;
            while (it.hasNext()) {
                ps.setObject(paramIndex, it.next());
                paramIndex++;
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(COLUMNS.ID.name);
            }
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, null, conn);
        }
        return id;
    }

    public boolean removeQueue(QueueData data) {
        String sql = "DELETE FROM " + tables.get(0) + " WHERE "
                + COLUMNS.X.name + " = ? AND "
                + COLUMNS.Z.name + " = ? AND "
                + COLUMNS.WORLD.name + " = ?"
                ;
        Location loc = data.getLocation();
        List<Object> params = new ArrayList<Object>() {{
            add(loc.getBlockX());
            add(loc.getBlockZ());
            add(loc.getWorld().getName());
        }};
        return sqlUpdate(sql, params);
    }
}