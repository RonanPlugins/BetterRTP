package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import me.SuperRonanCraft.BetterRTPAddons.util.LocSerialization;
import me.SuperRonanCraft.BetterRTPAddons.database.Database;
import me.SuperRonanCraft.BetterRTPAddons.database.DatabaseColumn;
import me.SuperRonanCraft.BetterRTPAddons.database.Errors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FlashbackDatabase extends Database {

    enum Columns implements DatabaseColumn {
        UUID("uuid", "varchar(32)"),
        NAME("player", "varchar(16)"),
        LOCATION_OLD("location_old", "longtext"),
        TIME_GOAL("time_goal", "bigint(19)");

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

    List<FlashbackPlayerInfo> playerInfos = new ArrayList<>();

    public FlashbackDatabase(){
        super("addon_flashback");
    }

    private final String createTable = "CREATE TABLE IF NOT EXISTS " + table + " (" +
            "`" + Columns.UUID.name + "` " + Columns.UUID.type + " PRIMARY KEY NOT NULL," +
            "`" + Columns.NAME.name + "` " + Columns.NAME.type + "," +
            "`" + Columns.LOCATION_OLD.name + "` " + Columns.LOCATION_OLD.type + " NOT NULL," +
            "`" + Columns.TIME_GOAL.name + "` " + Columns.LOCATION_OLD.type + " NOT NULL" +
            ");";


    @Override
    public String getTableFormat() {
        return createTable;
    }

    @Override
    public void load(DatabaseColumn[] columns) {
        playerInfos.clear();
        super.load(columns);
    }

    public FlashbackPlayerInfo getPlayer(Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + Columns.UUID.name + " = ?");
            UUID id = p.getUniqueId();
            ps.setString(1, id.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                Location loc = LocSerialization.getLocationFromString(rs.getString(Columns.LOCATION_OLD.name));
                return new FlashbackPlayerInfo(p, loc);
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    public boolean setPlayer(Player p, Location oldLocation, Long timeGoal) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO " + table +
                    "(" +
                    Columns.UUID.name + ", " +
                    Columns.TIME_GOAL.name + ", " +
                    Columns.LOCATION_OLD.name +
                    ") VALUES (?, ?, ?) "
                    + "ON CONFLICT(" + Columns.UUID.name +
                    ") DO UPDATE SET " +
                    Columns.TIME_GOAL.name + " = + ?, " +
                    Columns.LOCATION_OLD.name + " = + ?"
            );
            UUID id = p.getUniqueId();
            ps.setString(1, id.toString());
            ps.setLong(2, timeGoal);
            String serialLocation = LocSerialization.getStringFromLocation(oldLocation);
            ps.setString(3, serialLocation);
            ps.setLong(4, timeGoal);
            ps.setString(5, serialLocation);
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
        } finally {
            close(ps, null, conn);
        }
        return success;
    }
}
