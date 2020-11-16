package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import me.SuperRonanCraft.BetterRTPAddons.PlayerInfo;
import me.SuperRonanCraft.BetterRTPAddons.database.Database;
import me.SuperRonanCraft.BetterRTPAddons.database.DatabaseColumn;

import java.util.ArrayList;
import java.util.List;

public class PortalsDatabase extends Database {

    enum Columns implements DatabaseColumn {
        ID("portal", "integer"),
        LOCATION_1("location_1", "longtext"),
        LOCATION_2("location_2", "longtext"),
        TITLE("title", "varchar(255)");

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

    List<PlayerInfo> playerInfos = new ArrayList<>();

    public PortalsDatabase(){
        super("addon_portals");
    }

    private final String createTable = "CREATE TABLE IF NOT EXISTS " + table + " (" +
            "`" + Columns.ID.name + "` " + Columns.ID.type + " PRIMARY KEY AUTOINCREMENT," +
            "`" + Columns.LOCATION_1.name + "` " + Columns.LOCATION_2.type + "," +
            "`" + Columns.LOCATION_2.name + "` " + Columns.LOCATION_2.type + "," +
            "`" + Columns.TITLE.name + "` " + Columns.TITLE.type +
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

    /*public PlayerInfo getPlayer(Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + Columns.UUID.name + " = ?");
            UUID id = p.getUniqueId();
            ps.setString(1, id != null ? id.toString() : console_id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Location loc = LocSerialization.getLocationFromString(rs.getString(Columns.LOCATION_OLD.name));
                return new PlayerInfo(p, loc);
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    public boolean setPlayer(Player p, Location oldLocation) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO " + table + "(" + Columns.UUID.name + ", " + Columns.LOCATION_OLD.name + ") VALUES (?, ?) "
                    + "ON CONFLICT(" + Columns.UUID.name + ") DO UPDATE SET " + Columns.LOCATION_OLD.name + " = + ?");
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
    }*/
}
