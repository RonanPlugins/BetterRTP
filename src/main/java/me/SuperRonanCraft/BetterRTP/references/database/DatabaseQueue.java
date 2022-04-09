package me.SuperRonanCraft.BetterRTP.references.database;

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
                //String id = rs.getString(COLUMNS.IDENTIFIER.name);
                long generated = rs.getLong(COLUMNS.GENERATED.name);
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    QueueData data = new QueueData(new Location(world, x, 69 /*giggity*/, z), generated);
                    queueDataList.add(data);
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
    public boolean addQueue(QueueData data) {
        String pre = "INSERT INTO ";
        String sql = pre + tables.get(0) + " ("
                + COLUMNS.X.name + ", "
                + COLUMNS.Z.name + ", "
                + COLUMNS.WORLD.name + ", "
                + COLUMNS.GENERATED.name + " "
                //+ COLUMNS.USES.name + " "
                + ") VALUES(?, ?, ?, ?)";
        List<Object> params = new ArrayList<Object>() {{
                add(data.getLocation().getX());
                add(data.getLocation().getZ());
                add(data.getLocation().getWorld().getName());
                add(data.getGenerated());
                //add(data.getUses());
        }};
        return sqlUpdate(sql, params);
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