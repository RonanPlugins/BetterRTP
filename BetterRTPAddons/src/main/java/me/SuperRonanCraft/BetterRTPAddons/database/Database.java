package me.SuperRonanCraft.BetterRTPAddons.database;

import me.SuperRonanCraft.BetterRTPAddons.Main;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public abstract class Database {

    public String console_id = "CONSOLE";
    public Connection connection;
    public Main plugin;
    public String db_name;
    public final String table = "BRTP_Data";

    public Database(String db_name) {
        plugin = Main.getInstance();
        this.db_name = db_name;
    }

    protected Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder().getPath() + File.separator + "data", db_name + ".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.getParentFile().mkdirs();
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + db_name + ".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    //Get the layout of the database table
    public abstract String getTableFormat();

    public void load(DatabaseColumn[] columns) {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(getTableFormat());
            for (DatabaseColumn c : columns) { //Add missing columns dynamically
                try {
                    String addMissingColumns = "ALTER TABLE " + table + " ADD COLUMN %column% %type%";
                    s.executeUpdate(addMissingColumns.replace("%column%", c.getName()).replace("%type%", c.getType()));
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            }
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize(columns[0]);
    }

    private void initialize(DatabaseColumn testColumn) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + testColumn.getName() + " = ?");
            ps.setString(1, "");
            rs = ps.executeQuery();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        } finally {
            close(ps, rs, conn);
        }
    }

    //Processing
    protected void close(PreparedStatement ps, ResultSet rs, java.sql.Connection conn) {
        try {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
            if (rs != null) rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}