package me.SuperRonanCraft.BetterRTP.references.database;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class DatabaseCooldownsGlobal extends SQLite {

    public DatabaseCooldownsGlobal() {
        super(DATABASE_TYPE.COOLDOWN_GLOBAL);
    }

    @Override
    public List<String> getTables() {
        return Collections.singletonList("BetterRTP_Cooldown");
    }

    public enum COLUMNS {
        UUID("uuid", "varchar(32) PRIMARY KEY"),
        //COOLDOWN DATA
        COOLDOWN_DATE("date", "long"),
        USES("uses", "integer"),
        ;

        public final String name;
        public final String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public boolean removePlayer(UUID uuid) {
        String sql = "DELETE FROM " + tables.get(0) + " WHERE "
                + COLUMNS.UUID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(uuid.toString());
        }};
        return sqlUpdate(sql, params);
    }

    public CooldownData getCooldown(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tables.get(0) + " WHERE " + COLUMNS.UUID.name + " = ?");
            ps.setString(1, uuid.toString());

            rs = ps.executeQuery();
            if (rs.next()) {
                Long time = rs.getLong(COLUMNS.COOLDOWN_DATE.name);
                int uses = rs.getInt(COLUMNS.USES.name);
                return new CooldownData(uuid, time, uses, null);
            }
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    //Set a player Cooldown
    public void setCooldown(CooldownData data) {
        String pre = "INSERT OR REPLACE INTO ";
        String sql = pre + tables.get(0) + " ("
                + COLUMNS.UUID.name + ", "
                + COLUMNS.COOLDOWN_DATE.name + ", "
                + COLUMNS.USES.name + " "
                + ") VALUES(?, ?, ?)";
        List<Object> params = new ArrayList<Object>() {{
                add(data.getUuid().toString());
                add(data.getTime());
                add(data.getUses());
        }};
        sqlUpdate(sql, params);
    }

    //Update multiple players cooldowns
    public void setCooldown(List<CooldownData> cooldownData) {
        String pre = "INSERT OR REPLACE INTO ";
        String sql = pre + tables.get(0) + " ("
                + COLUMNS.UUID.name + ", "
                + COLUMNS.COOLDOWN_DATE.name + ", "
                + COLUMNS.USES.name + " "
                + ") VALUES(?, ?, ?)";
        for (CooldownData data : cooldownData) {
            List<Object> param = new ArrayList<Object>() {{
                add(data.getUuid().toString());
                add(data.getTime());
                add(data.getUses());
            }};
            sqlUpdate(sql, param);
        }
    }
}