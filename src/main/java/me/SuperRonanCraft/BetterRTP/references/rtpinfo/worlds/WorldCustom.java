package me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds;

import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_SHAPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class WorldCustom implements RTPWorld, RTPWorld_Defaulted {
    public World world;
    private boolean useWorldborder;
    private int centerX, centerZ, maxRad, minRad, price, miny, maxy;
    private List<String> biomes;
    private RTP_SHAPE shape;

    public WorldCustom(World world) {
        //String pre = "CustomWorlds.";
        FileBasics.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        List<Map<?, ?>> map = config.getMapList("CustomWorlds");
        this.world = world;

        //Set Defaults
        setupDefaults();

        //Find Custom World and cache values
        for (Map<?, ?> m : map) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                String key = entry.getKey().toString();
                if (!key.equals(world.getName()))
                    continue;
                Map<?, ?> test = ((Map<?, ?>) m.get(key));
                if (test == null)
                    continue;
                if (test.get("UseWorldBorder") != null) {
                    if (test.get("UseWorldBorder").getClass() == Boolean.class)
                        useWorldborder = Boolean.parseBoolean(test.get("UseWorldBorder").toString());
                }
                if (test.get("CenterX") != null) {
                    if (test.get("CenterX").getClass() == Integer.class)
                        centerX = Integer.parseInt((test.get("CenterX")).toString());
                }
                if (test.get("CenterZ") != null) {
                    if (test.get("CenterZ").getClass() == Integer.class)
                        centerZ = Integer.parseInt((test.get("CenterZ")).toString());
                }
                if (test.get("MaxRadius") != null) {
                    if (test.get("MaxRadius").getClass() == Integer.class)
                        maxRad = Integer.parseInt((test.get("MaxRadius")).toString());
                    if (maxRad <= 0) {
                        BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Custom world '" + world + "' Maximum radius of '" + maxRad + "' is not allowed! Set to default value!");
                        maxRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMaxRadius();
                    }
                }
                if (test.get("MinRadius") != null) {
                    if (test.get("MinRadius").getClass() == Integer.class)
                        minRad = Integer.parseInt((test.get("MinRadius")).toString());
                    if (minRad < 0 || minRad >= maxRad) {
                        BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                                "WARNING! Custom world '" + world + "' Minimum radius of '" + minRad + "' is not allowed! Set to default value!");
                        minRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMinRadius();
                        if (minRad >= maxRad)
                            maxRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMaxRadius();
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
                            //price = worldDefault.getPrice(world);
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
                if (test.get("MinY") != null) {
                    if (test.get("MinY").getClass() == Integer.class)
                       this.miny = Integer.parseInt((test.get("MinY")).toString());
                }
                if (test.get("MaxY") != null) {
                    if (test.get("MaxY").getClass() == Integer.class)
                       this.maxy = Integer.parseInt((test.get("MaxY")).toString());
                }
            }
        }
        //Booleans
        /*useWorldborder = config.getBoolean(pre + world + ".UseWorldBorder");
        //Integers
        CenterX = config.getInt(pre + world + ".CenterX");
        CenterZ = config.getInt(pre + world + ".CenterZ");
        maxBorderRad = config.getInt(pre + world + ".MaxRadius");*/
        if (maxRad <= 0) {
            BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Custom world '" + world + "' Maximum radius of '" + maxRad + "' is not allowed! Set to default value!");
            maxRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMaxRadius();
        }
        //minBorderRad = config.getInt(pre + world + ".MinRadius");
        if (minRad < 0 || minRad >= maxRad) {
            BetterRTP.getInstance().getText().sms(Bukkit.getConsoleSender(),
                    "WARNING! Custom world '" + world + "' Minimum radius of '" + minRad + "' is not allowed! Set to default value!");
            minRad = BetterRTP.getInstance().getRTP().RTPdefaultWorld.getMinRadius();
        }
        /*if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
            if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("CustomWorlds.Enabled")) {
                List<Map<?, ?>> world_map = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getMapList("CustomWorlds.Worlds");
                for (Map<?, ?> m : world_map)
                    for (Map.Entry<?, ?> entry : m.entrySet()) {
                        String _world = entry.getKey().toString();
                        //System.out.println("Custom World Price " + _world + ":" + entry.getValue().toString());
                        if (!_world.equals(world))
                            continue;
                        if (entry.getValue().getClass() == Integer.class)
                            price = Integer.parseInt((entry.getValue().toString()));
                    }
            } else
                price = worldDefault.getPrice();*/
        //Other
        //this.Biomes = config.getStringList("CustomWorlds." + world + ".Biomes");

        if (BetterRTP.getInstance().getSettings().isDebug()) {
            Logger log = BetterRTP.getInstance().getLogger();
            log.info("- -World: " + this.world.getName());
            log.info("- UseWorldBorder: " + this.useWorldborder);
            log.info("- CenterX: " + this.centerX);
            log.info("- CenterZ: " + this.centerZ);
            log.info("- MaxRadius: " + this.maxRad);
            log.info("- MinRadius: " + this.minRad);
            log.info("- Price: " + this.price);
            log.info("- MinY: " + this.miny);
            log.info("- MaxY: " + this.maxy);
        }
    }

    public WorldCustom(World world, RTPWorld rtpWorld) {
        setAllFrom(rtpWorld);
        this.world = world;
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

    @NotNull
    @Override
    public World getWorld() {
        return world;
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

    //Setters
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
    public void setWorld(World value) {
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
}
