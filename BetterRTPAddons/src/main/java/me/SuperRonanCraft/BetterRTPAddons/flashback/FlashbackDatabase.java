package me.SuperRonanCraft.BetterRTPAddons.flashback;

import me.SuperRonanCraft.BetterRTPAddons.LocSerialization;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.PlayerInfo;
import me.SuperRonanCraft.BetterRTPAddons.database.Database;
import me.SuperRonanCraft.BetterRTPAddons.database.Errors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FlashbackDatabase extends Database {

    List<PlayerInfo> playerInfos = new ArrayList<>();

    public FlashbackDatabase(Main instance){
        super(instance, "addon_flashback");
    }

    private final String createTable = "CREATE TABLE IF NOT EXISTS " + table + " (" +
            "`" + COLUMNS.UUID.name + "` " + COLUMNS.UUID.type + " PRIMARY KEY NOT NULL," +
            "`" + COLUMNS.NAME.name + "` " + COLUMNS.NAME.type + "," +
            "`" + COLUMNS.LOCATION_OLD.name + "` " + COLUMNS.LOCATION_OLD.type + " NOT NULL," +
            "`" + COLUMNS.TIME_GOAL.name + "` " + COLUMNS.LOCATION_OLD.type + " NOT NULL" +
            ");";


    @Override
    public String getTableFormat() {
        return createTable;
    }

    @Override
    public void load() {
        playerInfos.clear();
        super.load();
    }

    public PlayerInfo getPlayer(Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + COLUMNS.UUID.name + " = ?");
            UUID id = p.getUniqueId();
            ps.setString(1, id != null ? id.toString() : console_id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Location loc = LocSerialization.getLocationFromString(rs.getString(COLUMNS.LOCATION_OLD.name));
                return new PlayerInfo(p, loc);
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
            ps = conn.prepareStatement("INSERT INTO " + table + "(" + COLUMNS.UUID.name + ", " + COLUMNS.LOCATION_OLD.name + ") VALUES (?, ?) "
                    + "ON CONFLICT(" + COLUMNS.UUID.name + ") DO UPDATE SET " + COLUMNS.LOCATION_OLD.name + " = + ?");
            UUID id = p.getUniqueId();
            ps.setString(1, id != null ? id.toString() : console_id);
            String serialLocation = LocSerialization.getStringFromLocation(oldLocation);
            ps.setString(2, serialLocation);
            ps.setString(3, serialLocation);
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
