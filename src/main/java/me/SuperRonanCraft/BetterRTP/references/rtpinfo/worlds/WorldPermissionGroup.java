package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import lombok.Getter;
import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

@SuppressWarnings("rawtypes")
public class WorldPermissionGroup implements RTPWorld, RTPWorld_Defaulted {
    private boolean useWorldborder;
    private int centerX, centerZ, maxRad, minRad, price, miny, maxy;
    private List<String> biomes;
    private String world;
    private RTP_SHAPE shape;
    @Getter private int priority;

    /*public RTPPermConfiguration getGroup(CommandSender p) {
        for (RTPPermConfiguration group : groups)
            if (BetterRTP.getInstance().getPerms().getPermissionGroup(p, group.name))
                return group;
        return null;
    }*/

    public WorldPermissionGroup(String group) {
        FileBasics.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.LOCATIONS);
        List<Map<?, ?>> map = config.getMapList("PermissionGroup.Groups");

        setupDefaults();

        this.priority = 0;
        //Find Location and cache its values
        for (Map<?, ?> m : map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                String key = entry.getKey().toString();
                if (!key.equals(group))
                    continue;
                Map<?, ?> test = ((Map<?, ?>) m.get(key));
                if (test == null)
                    continue;
                if (test.get("Priority") != null) {
                    if (test.get("Priority").getClass() == Integer.class)
                        priority = Integer.parseInt((test.get("Priority")).toString());
                }
                if (test.get("World") != null) {
                    if (test.get("World").getClass() == String.class)
                        world = test.get("World").toString();
                } else {
                    BetterRTP.getInstance().getLogger().warning("Group `" + group + "` does NOT have a World specified!");
                    return;
                }
                if (test.get("UseWorldBorder") != null) {
                    if (test.get("UseWorldBorder").getClass() == Boolean.class)
                        useWorldborder = Boolean.parseBoolean(test.get("UseWorldBorder").toString());
                }
                if (test.get("CenterX") != null) {
                    if (test.get("CenterX").getClass() == Integer.class)
                        centerX = Integer.parseInt((test.get("CenterX")).toString());
                }
                if (test.get("CenterZ") != null) {
                    if (test.get("CenterZ").getClass() == Integer.class) {
                        centerZ = Integer.parseInt((test.get("CenterZ")).toString());
                    }
                }
                if (test.get("MaxRadius") != null) {
                    if (test.get("MaxRadius").getClass() == Integer.class)
                        maxRad = Integer.parseInt((test.get("MaxRadius")).toString());
                    if (maxRad <= 0) {
                        BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Group '" + group + "' Maximum radius of '" + maxRad + "' is not allowed! Set to default value!");
                        maxRad = BetterRTP.getInstance().getRTP().defaultWorld.getMaxRadius();
                    }
                }
                if (test.get("MinRadius") != null) {
                    if (test.get("MinRadius").getClass() == Integer.class)
                        minRad = Integer.parseInt((test.get("MinRadius")).toString());
                    if (minRad < 0 || minRad >= maxRad) {
                        BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Group '" + group + "' Minimum radius of '" + minRad + "' is not allowed! Set to default value!");
                        minRad = BetterRTP.getInstance().getRTP().defaultWorld.getMinRadius();
                        if (minRad >= maxRad)
                            maxRad = BetterRTP.getInstance().getRTP().defaultWorld.getMaxRadius();
                    }
                }
                if (test.get("Biomes") != null) {
                    if (test.get("Biomes").getClass() == ArrayList.class)
                        this.biomes = new ArrayList<String>((ArrayList) test.get("Biomes"));
                }
                if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
                    if (test.get("Price") != null) {
                        if (test.get("Price").getClass() == Integer.class)
                            this.price = Integer.parseInt(test.get("Price").toString());
                        //else
                        // price = worldDefault.getPrice(world);
                    } //else
                //price = worldDefault.getPrice(world);
                if (test.get("Shape") != null) {
                    if (test.get("Shape").getClass() == String.class) {
                        try {
                            this.shape = RTP_SHAPE.valueOf(test.get("Shape").toString().toUpperCase());
                        } catch (Exception e) {
                            //Invalid shape
                        }
                    }
                }
                if (test.get("UseWorldBorder") != null) {
                    if (test.get("UseWorldBorder").getClass() == Boolean.class) {
                        try {
                            this.useWorldborder = Boolean.parseBoolean(test.get("UseWorldBorder").toString());
                        } catch (Exception e) {
                            //No UseWorldBorder
                        }
                    }
                }
                if (test.get("MinY") != null)
                    if (test.get("MinY").getClass() == Integer.class)
                        this.miny = Integer.parseInt(test.get("MinY").toString());
                if (test.get("MaxY") != null)
                    if (test.get("MaxY").getClass() == Integer.class)
                        this.maxy = Integer.parseInt(test.get("MaxY").toString());
            }
        }
    }

    @Override
    public boolean getUseWorldborder() {
        return useWorldborder;
    }

    @Override
    public int getCenterX() {
        return centerX;
    }

    @Override
    public int getCenterZ() {
        return centerZ;
    }

    @Override
    public int getMaxRadius() {
        return maxRad;
    }

    @Override
    public int getMinRadius() {
        return minRad;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public List<String> getBiomes() {
        return biomes;
    }

    @Override
    public @NonNull World getWorld() {
        return Bukkit.getWorld(world);
    }

    @Override
    public RTP_SHAPE getShape() {
        return shape;
    }

    @Override
    public int getMinY() {
        return miny;
    }

    @Override
    public int getMaxY() {
        return maxy;
    }

    @Override
    public void setUseWorldBorder(boolean value) {
        this.useWorldborder = value;
    }

    @Override
    public void setCenterX(int value) {
        this.centerX = value;
    }

    @Override
    public void setCenterZ(int value) {
        this.centerZ = value;
    }

    @Override
    public void setMaxRadius(int value) {
        this.maxRad = value;
    }

    @Override
    public void setMinRadius(int value) {
        this.minRad = value;
    }

    @Override
    public void setPrice(int value) {
        this.price = value;
    }

    @Override
    public void setBiomes(List<String> value) {
        this.biomes = value;
    }

    @Override
    public void setWorld(String value) {
        this.world = value;
    }

    @Override
    public void setShape(RTP_SHAPE value) {
        this.shape = value;
    }

    @Override
    public void setMinY(int value) {
        this.miny = value;
    }

    @Override
    public void setMaxY(int value) {
        this.maxy = value;
    }

    /*public static class RTPPermConfiguration {

        boolean valid;
        public String name;
        public List<RTPPermConfigurationWorld> worlds = new ArrayList<>();

        RTPPermConfiguration(Map.Entry<?, ?> fields) {
            String group = fields.getKey().toString();
            Object value = fields.getValue();
            for (Object worlds : ((ArrayList) value)) {
                for (Object hash : ((HashMap) worlds).entrySet()) {
                    RTPPermConfigurationWorld worldConfig = new RTPPermConfigurationWorld(hash, group);
                    if (worldConfig.isValid())
                        this.worlds.add(worldConfig);
                    else
                        BetterRTP.debug("ERROR! Group " + group + " world " + worldConfig.name + " was not setup correctly!");
                }
            }
            this.name = group;
            valid = worlds.size() > 0 && group != null;
        }

        boolean isValid() {
            return valid;
        }
    }

    public static class RTPPermConfigurationWorld {

        boolean valid;

        public int maxRad = -1;
        public int minRad = -1;
        public int price = -1;
        public int centerx = -1;
        public int centerz = -1;
        public Object useworldborder = null;

        public String name;

        RTPPermConfigurationWorld(Object hash, String group) {
            Map.Entry world = (Map.Entry) hash;
            this.name = world.getKey().toString();
            //Main.getInstance().getLogger().info("World added to '" + group +"': '" + world.getKey() + "'");
            for (Object hash2 : ((HashMap) world.getValue()).entrySet()) {
                Map.Entry hash3 = (Map.Entry) hash2;
                String field = hash3.getKey().toString();
                if (field.equalsIgnoreCase("MaxRadius")) { //MaxRadius
                    maxRad = getInt(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("MinRadius")) { //MinRadius
                    minRad = getInt(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("Price")) { //MinRadius
                    price = getInt(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("UseWorldBorder")) { //UseWorldBorder
                    useworldborder = Boolean.valueOf(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("CenterX")) { //Center X
                    centerx = getInt(hash3.getValue().toString());
                } else if (field.equalsIgnoreCase("CenterZ")) { //Center Z
                    centerz = getInt(hash3.getValue().toString());
                }
            }
            //Main.getInstance().getLogger().info("World MaxRad '" + world.getKey() + "' is " + maxRad);
            //Main.getInstance().getLogger().info("World MinRad '" + world.getKey() + "' is " + minRad);
            valid = this.name != null && (minRad != -1 || maxRad != -1);
        }

        private int getInt(String input) {
            return Integer.parseInt(input);
        }

        boolean isValid() {
            return valid;
        }
    }*/
}
