package me.SuperRonanCraft.BetterRTP.references;

import me.SuperRonanCraft.BetterRTP.BetterRTP;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class RTPLogger {

    static Handler fileHandler = null;
    private Logger LOGGER;

    public void setup(BetterRTP plugin) {
        this.LOGGER = plugin.getLogger();
        if (plugin.getSettings().isDebug())
            LOGGER.setLevel(Level.ALL);
        else
            LOGGER.setLevel(Level.WARNING);
        try {
            fileHandler = new FileHandler(plugin.getDataFolder().getAbsoluteFile() + File.separator + "data.log");
            SimpleFormatter simple = new SimpleFormatter();
            fileHandler.setFormatter(simple);
            LOGGER.addHandler(fileHandler);//adding Handler for file
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

}
