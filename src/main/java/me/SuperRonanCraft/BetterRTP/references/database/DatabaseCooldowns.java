package me.SuperRonanCraft.BetterRTP.references.database;

import me.ronancraft.AmethystGear.AmethystGear;
import me.ronancraft.AmethystGear.resources.gear.ELEMENT_TYPE;
import me.ronancraft.AmethystGear.resources.gear.GEAR_TIER;
import me.ronancraft.AmethystGear.resources.gear.GEAR_TYPE;
import me.ronancraft.AmethystGear.resources.gear.catalysts.Catalyst;
import me.ronancraft.AmethystGear.resources.gear.gear.GearBaseInfo;
import me.ronancraft.AmethystGear.resources.gear.gear.GearData;
import me.ronancraft.AmethystGear.resources.helpers.HelperDate;
import me.ronancraft.AmethystGear.resources.helpers.HelperJsonConverter;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class DatabaseCooldowns extends SQLite {

    public DatabaseCooldowns() {
        super(DATABASE_TYPE.COOLDOWN);
    }

    public enum COLUMNS {
        INDEX("id", "integer PRIMARY KEY AUTOINCREMENT"),
        UUID("owner", "varchar(32)"),
        //GEAR INFO
        GEAR_TIER("gear_tier", "text DEFAULT 'BRONZE'"),
        GEAR_LEVEL("level", "integer DEFAULT 1"),
        GEAR_XP("xp", "integer DEFAULT 0"),
        GEAR_CATALYST("catalyst", "text DEFAULT NULL"),
        GEAR_TRACKER("trackers", "text DEFAULT NULL"), //Trackers stored in json
        //BASIC GEAR INFO
        IDENTIFIER("identifier", "varchar(32)"),
        BASE_TIER("base_tier", "text DEFAULT 'BRONZE'"),
        BASE_ELEMENT("base_element", "text DEFAULT 'WATER'"),
        BASE_TYPE("base_type", "text DEFAULT 'SWORD'"),
        //MISC
        DATE_ACQUIRED("date_acquired", "text DEFAULT 'N/A'"),
        FAVORITE("favorite", "boolean DEFAULT false"),
        ;

        public String name;
        public String type;

        COLUMNS(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public boolean removeGear(GearData gear) {
        String sql = "DELETE FROM " + table + " WHERE "
                + COLUMNS.INDEX.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(gear.getDatabase_id());
        }};
        return sqlUpdate(sql, params);
    }

    public boolean removeGear(List<GearData> gear) {
        String sql = "DELETE FROM " + table + " WHERE "
                + COLUMNS.INDEX.name + " IN (" + makePlaceholders(gear.size()) + ")";
        List<Object> params = new ArrayList<>();
        for (GearData gearData : gear)
            params.add(gearData.getDatabase_id());
        return sqlUpdate(sql, params);
    }

    private static String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++)
            sb.append(",?");
        return sb.toString();
    }

    public List<GearData> getGear(Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<GearData> gearList = new ArrayList<>();
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + COLUMNS.UUID.name + " = ?");
            ps.setString(1, p.getUniqueId().toString());

            rs = ps.executeQuery();
            //Load all of players gear
            while (rs.next()) {
                GearBaseInfo baseInfo = new GearBaseInfo(
                        rs.getString(COLUMNS.IDENTIFIER.name),
                        ELEMENT_TYPE.valueOf(rs.getString(  COLUMNS.BASE_ELEMENT.name)),
                        GEAR_TIER.valueOf(rs.getString(     COLUMNS.BASE_TIER.name)),
                        GEAR_TYPE.valueOf(rs.getString(     COLUMNS.BASE_TYPE.name))
                );
                GearData data = new GearData(
                        baseInfo,
                        rs.getInt(                          COLUMNS.INDEX.name),
                        rs.getString(                       COLUMNS.DATE_ACQUIRED.name),
                        GEAR_TIER.valueOf(rs.getString(     COLUMNS.GEAR_TIER.name)),
                        rs.getInt(                          COLUMNS.GEAR_LEVEL.name),
                        rs.getInt(                          COLUMNS.GEAR_XP.name),
                        HelperJsonConverter.getCatalystFromJson(rs.getString(COLUMNS.GEAR_CATALYST.name)),
                        HelperJsonConverter.getTrackersFromJson(rs.getString(COLUMNS.GEAR_TRACKER.name))
                );
                data.setFavorite(rs.getBoolean(COLUMNS.FAVORITE.name));
                if (data.getGear().getIdentifier() != null)
                    gearList.add(data);
            }
            return gearList;
        } catch (SQLException ex) {
            AmethystGear.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, rs, conn);
        }
        return null;
    }

    public void fixGear(Player player) {
        //This is here incase in the future someone smart breaks my database and I gotta fix it in code...
    }

    //Create gear
    public GearData addNewGear(Player p, GearBaseInfo _gear) {
        String date = HelperDate.getDate(HelperDate.getDate());
        GearData gearData = new GearData(_gear, date);
        //Default Catalyst
        gearData.setCatalysts(List.of(new Catalyst(1, _gear.getElement())));

        String pre = "INSERT INTO ";
        String sql = pre + table + " ("
                + COLUMNS.UUID.name + ", "
                //GEAR INFO
                + COLUMNS.GEAR_TIER.name + ", "
                + COLUMNS.GEAR_LEVEL.name + ", "
                + COLUMNS.GEAR_XP.name + ", "
                + COLUMNS.GEAR_CATALYST.name + ", "
                + COLUMNS.GEAR_TRACKER.name + ", "
                //BASE GEAR INFO
                + COLUMNS.IDENTIFIER.name + ", "
                + COLUMNS.BASE_ELEMENT.name + ", "
                + COLUMNS.BASE_TYPE.name + ", "
                + COLUMNS.BASE_TIER.name + ", "
                //MISC
                + COLUMNS.DATE_ACQUIRED.name + ", "
                + COLUMNS.FAVORITE.name + " "
                + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>() {{
                add(p.getUniqueId().toString());
                //GEAR INFO
                add(gearData.getTier().name());
                add(gearData.getLevel());
                add(gearData.getXp());
                add(HelperJsonConverter.getJsonFromCatalysts(gearData.getCatalysts()));
                add(HelperJsonConverter.getJsonFromTrackers(gearData.getTrackers()));
                //BASE GEAR INFO
                add(gearData.getGear().getIdentifier());
                add(gearData.getGear().getElement().name());
                add(gearData.getGear().getType().name());
                add(gearData.getGear().getTier().name());
                //MISC
                add(date);
                add(gearData.isFavorite());
        }};
        gearData.setDatabase_id(sqlGetIndex(sql, params));

        return gearData;
    }

    public boolean updateGear(GearData gear) {
        String sql = "UPDATE " + table + " SET "
                + COLUMNS.GEAR_TIER.name + " = ?, "
                + COLUMNS.GEAR_LEVEL.name + " = ?, "
                + COLUMNS.GEAR_XP.name + " = ?, "
                + COLUMNS.GEAR_CATALYST.name + " = ?, "
                + COLUMNS.GEAR_TRACKER.name + " = ?, "
                + COLUMNS.FAVORITE.name + " = ? "
                + "WHERE "
                + COLUMNS.INDEX.name + " = ?";
        List<Object> params = new ArrayList<>() {{
            add(gear.getTier().name());
            add(gear.getLevel());
            add(gear.getXp());
            add(HelperJsonConverter.getJsonFromCatalysts(gear.getCatalysts()));
            add(HelperJsonConverter.getJsonFromTrackers(gear.getTrackers()));
            add(gear.isFavorite());
            //Database Index
            add(gear.getDatabase_id());
        }};
        gear.setUpdated(false); //Gear has been uploaded, no need to update if nothing new has changed
        return sqlUpdate(sql, params);
    }

    private int sqlGetIndex(String statement, List<Object> params) {
        Connection conn = null;
        PreparedStatement ps = null;
        int index = -1;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            if (params != null) {
                Iterator<Object> it = params.iterator();
                int paramIndex = 1;
                while (it.hasNext()) {
                    ps.setObject(paramIndex, it.next());
                    paramIndex++;
                }
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                //HelperItemGeneral.applyData(HelperData.getData(gear.item), GENERAL_DATA_INT.DATABASE_INDEX, rs.getInt(1));
                index = rs.getInt(1);
            }
        } catch (SQLException ex) {
            AmethystGear.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, null, conn);
        }
        return index;
    }
}