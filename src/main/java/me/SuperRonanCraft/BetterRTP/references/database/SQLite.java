package me.SuperRonanCraft.BetterRTP.references.database;

import lombok.Getter;
import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public abstract class SQLite {

    private static final String db_file_name = "database";
    @Getter List<String> tables;
    //private String host, database, username, password;
    //private int port;
    //boolean sqlEnabled;
    private boolean loaded;

    public String addMissingColumns = "ALTER TABLE %table% ADD COLUMN %column% %type%";

    private final DATABASE_TYPE type;

    public SQLite(DATABASE_TYPE type) {
        this.type = type;
    }

    public abstract List<String> getTables();

    // SQL creation stuff
    public Connection getSQLConnection() {
        /*if (sqlEnabled) {
            try {
                return getOnline();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                BetterRTP.getInstance().getLogger().info("MySQL setup is incorrect! Grabbing data from local database!");
                sqlEnabled = false;
            }
        }*/
        return getLocal();
    }

    /*private Connection getOnline() throws SQLException, ClassNotFoundException {
        synchronized (this) {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database +
                    "?autoReconnect=true&useSSL=false", this.username, this.password);
        }
    }*/

    private Connection getLocal() {
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
            return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
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
        /*switch (type) {
            case COOLDOWN: table = "BetterRTP_Cooldown"; break;
        }
        if (table == null) {
            BetterRTP.getInstance().getLogger().severe("The table for `" + type.name() + "` is invalid. Disabling the plugin!");
            Bukkit.getPluginManager().disablePlugin(BetterRTP.getInstance());
            return;
        }*/
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
            case PLAYERS: return DatabasePlayers.COLUMNS.values();
            case QUEUE: return DatabaseQueue.COLUMNS.values();
            case COOLDOWN:
            default: return DatabaseCooldowns.COLUMNS.values();
        }
    }

    private String getColumnName(DATABASE_TYPE type, Enum<?> column) {
        switch (type) {
            case PLAYERS: return ((DatabasePlayers.COLUMNS) column).name;
            case QUEUE: return ((DatabaseQueue.COLUMNS) column).name;
            case COOLDOWN:
            default: return ((DatabaseCooldowns.COLUMNS) column).name;
        }
    }

    private String getColumnType(DATABASE_TYPE type, Enum<?> column) {
        switch (type) {
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
            for (int i = 0; i < statement1.size(); i++) {
                String statement = statement1.get(i);
                List<Object> params = params1.get(i);
                if (ps == null)
                    ps = conn.prepareStatement(statement);
                else
                    ps.addBatch(statement);
                if (params != null) {
                    Iterator<Object> it = params.iterator();
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
            if (conn != null) conn.close();
            if (rs != null) rs.close();
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
    }
}
