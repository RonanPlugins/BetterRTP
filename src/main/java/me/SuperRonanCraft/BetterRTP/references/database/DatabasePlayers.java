package me.SuperRonanCraft.BetterRTP.references.database;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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
        //USES("uses", "integer"),
        ;

        public final String name;
        public final String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public boolean removePlayer(UUID uuid, World world) {
        String sql = "DELETE FROM " + world.getName() + " WHERE "
                + COLUMNS.UUID.name + " = ?";
        List<Object> params = new ArrayList<Object>() {{
            add(uuid.toString());
        }};
        return sqlUpdate(sql, params);
    }

    public int getCount(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tables.get(0) + " WHERE " + COLUMNS.UUID.name + " = ?");
            ps.setString(1, uuid.toString());

            rs = ps.executeQuery();
            if (rs.next()) {
                long time = rs.getLong(COLUMNS.COUNT.name);
                //int uses = rs.getInt(COLUMNS.USES.name);
                return Math.toIntExact(time);
            }
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return 0;
    }

    //Set a player Cooldown
    public void setCount(PlayerData data) {
        String pre = "INSERT OR REPLACE INTO ";
        String sql = pre + tables.get(0) + " ("
                + COLUMNS.UUID.name + ", "
                + COLUMNS.COUNT.name + " "
                //+ COLUMNS.USES.name + " "
                + ") VALUES(?, ?)";
        List<Object> params = new ArrayList<Object>() {{
                add(data.player.getUniqueId().toString());
                add(data.getRtpCount());
                //add(data.getUses());
        }};
        sqlUpdate(sql, params);
    }

    //Update multiple players cooldowns
    /*public void setCooldown(List<CooldownData> cooldownData) {
        String pre = "INSERT OR REPLACE INTO ";
        String sql = pre + table + " ("
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
    }*/
}