package me.SuperRonanCraft.BetterRTP.references.database;

import me.SuperRonanCraft.BetterRTP.BetterRTP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseCooldowns extends SQLite {

    public DatabaseCooldowns() {
        super(DATABASE_TYPE.COOLDOWN);
    }

    public enum COLUMNS {
        UUID("uuid", "varchar(32) PRIMARY KEY"),
        //COOLDOWN DATA
        COOLDOWN_DATE("date", "long"),
        USES("uses", "integer"),
        ;

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public boolean removePlayer(UUID uuid) {
        String sql = "DELETE FROM " + table + " WHERE "
                + COLUMNS.UUID.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(uuid.toString());
        }};
        return sqlUpdate(sql, params);
    }

    public List<Object> getCooldown(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + COLUMNS.UUID.name + " = ?");
            ps.setString(1, uuid.toString());

            rs = ps.executeQuery();
            if (rs.next()) {
                List<Object> data = new ArrayList<>();
                data.add(rs.getLong(COLUMNS.COOLDOWN_DATE.name));
                data.add(rs.getInt(COLUMNS.USES.name));
                return data;
            }
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    //Set a player Cooldown
    public void setCooldown(UUID uuid, Long time, int uses) {
        String pre = "INSERT OR REPLACE INTO ";
        String sql = pre + table + " ("
                + COLUMNS.UUID.name + ", "
                + COLUMNS.COOLDOWN_DATE.name + ", "
                + COLUMNS.USES.name + " "
                + ") VALUES(?, ?, ?)";
        List<Object> params = new ArrayList<>() {{
                add(uuid.toString());
                add(time);
                add(uses);
        }};
        sqlUpdate(sql, params);
    }
}