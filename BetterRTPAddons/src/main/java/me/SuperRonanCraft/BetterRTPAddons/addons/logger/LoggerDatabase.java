package me.SuperRonanCraft.BetterRTPAddons.addons.logger;

import me.SuperRonanCraft.BetterRTPAddons.database.Database;
import me.SuperRonanCraft.BetterRTPAddons.database.DatabaseColumn;
import me.SuperRonanCraft.BetterRTPAddons.database.Errors;
import me.SuperRonanCraft.BetterRTPAddons.util.LocSerialization;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class LoggerDatabase extends Database {

    enum Columns implements DatabaseColumn {
        LOCATION_FROM("location_from", "longtext"),
        LOCATION_TO("location_to", "longtext"),
        UUID("uuid", "varchar(255)"),
        LOG_ID("log_id", "integer"),
        NAME("name", "varchar(255)");

        private final String name;
        private final String type;

        Columns(String name, String type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return type;
        }
    }

    public LoggerDatabase(){
        super("addon_logger");
    }

    private final String createTable = "CREATE TABLE IF NOT EXISTS " + table + " (" +
            "`" + Columns.LOG_ID.name + "` " + Columns.LOG_ID.type + " PRIMARY KEY AUTOINCREMENT," +
            "`" + Columns.UUID.name + "` " + Columns.UUID.type + "," +
            "`" + Columns.NAME.name + "` " + Columns.NAME.type + "," +
            "`" + Columns.LOCATION_FROM.name + "` " + Columns.LOCATION_TO.type + "," +
            "`" + Columns.LOCATION_TO.name + "` " + Columns.LOCATION_TO.type +
            ");";


    @Override
    public String getTableFormat() {
        return createTable;
    }

    @Override
    public void load(DatabaseColumn[] columns) {
        super.load(columns);
    }

    /*public List<PortalsRegionInfo> getPortals() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table);
            rs = ps.executeQuery();
            List<PortalsRegionInfo> list = new ArrayList<>();
            while (rs.next()) {
               PortalsRegionInfo info = new PortalsRegionInfo();
               info.setLoc1(LocSerialization.getLocationFromString(rs.getString(Columns.LOCATION_1.name)));
               info.setLoc2(LocSerialization.getLocationFromString(rs.getString(Columns.LOCATION_2.name)));
               info.setName(rs.getString(Columns.NAME.name));
               list.add(info);
            }
            return list;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }*/

    public void log(Player p, Location loc_from, Location loc_to) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO " + table + "(" +
                    Columns.UUID.name + ", " +
                    Columns.NAME.name + ", " +
                    Columns.LOCATION_FROM.name + ", " +
                    Columns.LOCATION_TO.name + ") " +
                    "VALUES (?, ?, ?, ?) "
                    );
            ps.setString(1, p.getUniqueId().toString());
            ps.setString(2, p.getName());
            String serialLocation_from = LocSerialization.getStringFromLocation(loc_from);
            String serialLocation_to = LocSerialization.getStringFromLocation(loc_to);
            ps.setString(3, serialLocation_from);
            ps.setString(4, serialLocation_to);
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, null, conn);
        }
    }
}
