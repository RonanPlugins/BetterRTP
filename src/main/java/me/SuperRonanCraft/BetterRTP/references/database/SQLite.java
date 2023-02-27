package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.Getter;
import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.RandomLocation;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public abstract class SQLite {

    private static final String db_file_name = "database";
    List<String> tables;
    private boolean loaded;

    public String addMissingColumns = "ALTER TABLE %table% ADD COLUMN %column% %type%";

    private final DATABASE_TYPE type;

    public SQLite(DATABASE_TYPE type) {
        this.type = type;
    }

    public abstract List<String> getTables();

    // SQL creation stuff
    public Connection getSQLConnection() {
        return getLocal();
    }

    private Connection con;

    private Connection getLocal() {
        if (con != null) {
            try {
                if (!con.isClosed())
                    return con;
            } catch (SQLException ignored) {
            }
        }
        File dataFolder = new File(BetterRTP.getInstance().getDataFolder().getPath() + File.separator + "data", db_file_name + ".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.getParentFile().mkdir();
                dataFolder.createNewFile();
            } catch (IOException e) {
                BetterRTP.getInstance().getLogger().log(Level.SEVERE, "File write error: " + dataFolder.getPath());
                e.printStackTrace();
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return con;
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it Ronan...");
        }
        return null;
    }

    public void load() {
        loaded = false;
        tables = getTables();
        Bukkit.getScheduler().runTaskAsynchronously(BetterRTP.getInstance(), () -> {
            Connection connection = getSQLConnection();
            try {
                Statement s = connection.createStatement();
                for (String table : tables) {
                    s.executeUpdate(getCreateTable(table));
                    //s.executeUpdate(createTable_bank);
                    for (Enum<?> c : getColumns(type)) { //Add missing columns dynamically
                        try {
                            String _name = getColumnName(type, c);
                            String _type = getColumnType(type, c);
                            //System.out.println("Adding " + _name);
                            s.executeUpdate(addMissingColumns.replace("%table%", table).replace("%column%", _name).replace("%type%", _type));
                        } catch (SQLException e) {
                            //e.printStackTrace();
                        }
                    }
                    BetterRTP.debug("Database " + type.name() + ":" + table + " configured and loaded!");
                }
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            initialize();
            loaded = true;
        });
    }

    private String getCreateTable(String table) {
        String str = "CREATE TABLE IF NOT EXISTS `" + table + "` (";
        Enum<?>[] columns = getColumns(type);
        for (Enum<?> c : columns) {
            String _name = getColumnName(type, c);
            String _type = getColumnType(type, c);
            str = str.concat("`" + _name + "` " + _type);
            if (c.equals(columns[columns.length - 1]))
                str = str.concat(")");
            else
                str = str.concat(", ");
        }
        //System.out.println("MySQL column string: `" + str + "`");
        return str;
    }

    private Enum<?>[] getColumns(DATABASE_TYPE type) {
        switch (type) {
            case CHUNK_DATA: return DatabaseChunkData.COLUMNS.values();
            case PLAYERS: return DatabasePlayers.COLUMNS.values();
            case QUEUE: return DatabaseQueue.COLUMNS.values();
            case COOLDOWN:
            default: return DatabaseCooldowns.COLUMNS.values();
        }
    }

    private String getColumnName(DATABASE_TYPE type, Enum<?> column) {
        switch (type) {
            case CHUNK_DATA: return ((DatabaseChunkData.COLUMNS) column).name;
            case PLAYERS: return ((DatabasePlayers.COLUMNS) column).name;
            case QUEUE: return ((DatabaseQueue.COLUMNS) column).name;
            case COOLDOWN:
            default: return ((DatabaseCooldowns.COLUMNS) column).name;
        }
    }

    private String getColumnType(DATABASE_TYPE type, Enum<?> column) {
        switch (type) {
            case CHUNK_DATA: return ((DatabaseChunkData.COLUMNS) column).type;
            case PLAYERS: return ((DatabasePlayers.COLUMNS) column).type;
            case QUEUE: return ((DatabaseQueue.COLUMNS) column).type;
            case COOLDOWN:
            default: return ((DatabaseCooldowns.COLUMNS) column).type;
        }
    }

    //Processing
    protected boolean sqlUpdate(String statement, @NonNull List<Object> params) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
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
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
        } finally {
            close(ps, null, conn);
        }
        return success;
    }

    boolean sqlUpdate(List<String> statement1, List<List<Object>> params1) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
        try {
            conn = getSQLConnection();
            String str = statement1.get(0);
            for (int i = 1; i < statement1.size(); i++) {
                String state = statement1.get(i);
                str = str.concat("; " + state);
            }
            Statement statement = conn.statement
            for (int i = 0; i < statement1.size(); i++) {
                List<Object> param = params1.get(i);
                if (param != null) {
                    Iterator<Object> it = param.iterator();
                    int paramIndex = 1;
                    while (it.hasNext()) {
                        ps.setObject(paramIndex, it.next());
                        paramIndex++;
                    }
                }
            }
            assert ps != null;
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            success = false;
            ex.printStackTrace();
        } finally {
            close(ps, null, conn);
        }
        return success;
    }

    public void initialize() { //Let in console know if its all setup or not
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tables.get(0) + " WHERE " + getColumnName(type, getColumns(type)[0]) + " = 0");

            rs = ps.executeQuery();
        } catch (SQLException ex) {
            BetterRTP.getInstance().getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        } finally {
            close(ps, rs, conn);
        }
    }

    protected void close(PreparedStatement ps, ResultSet rs, Connection conn) {
        try {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            Error.close(BetterRTP.getInstance(), ex);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public enum DATABASE_TYPE {
        PLAYERS,
        COOLDOWN,
        QUEUE,
        CHUNK_DATA,
    }
}
