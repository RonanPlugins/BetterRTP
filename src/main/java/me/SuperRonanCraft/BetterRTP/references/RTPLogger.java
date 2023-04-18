package me.SuperRonanCraft.BetterRTP.references;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class RTPLogger {

    @Getter private String format;
    @Getter private File file;
    private FileHandler handler;
    //private ConsoleHandler consoleHandler_rtp, consoleHandler_logger;

    public void setup(BetterRTP plugin) {
        FileOther.FILETYPE config = plugin.getFiles().getType(FileOther.FILETYPE.CONFIG);
        boolean enabled = config.getBoolean("Settings.Logger.Enabled");
        Logger logger = plugin.getLogger();
        logger.setUseParentHandlers(true);
        if (handler != null) {
            logger.removeHandler(handler);
            handler.close();
        }
        if (!enabled) return;
        this.format = config.getString("Settings.Logger.Format");
        boolean toConsole = config.getBoolean("Settings.Logger.LogToConsole");
        try {
            this.file = new File(plugin.getDataFolder() + File.separator + "log.txt");
            Files.deleteIfExists(file.toPath());
            this.handler = new FileHandler(file.getPath(), true);
            handler.setFormatter(new MyFormatter());
            logger.setUseParentHandlers(toConsole); //Disable logging to console
            logger.addHandler(handler);
            //Log copying
            //consoleHandler_logger = new MyConsole(this.logger, plugin.getName());
            //plugin.getLogger().addHandler(consoleHandler_logger);
            //consoleHandler_rtp = new MyConsole(this.logger, plugin.getName());
            //plugin.getLogger().addHandler(consoleHandler_rtp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat(BetterRTP.getInstance().getRtpLogger().getFormat());
        return format.format(new Date());
    }

    public void unload() {
        if (handler != null)
            handler.close();
    }

    static class MyFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return getDate() + " [" + record.getLevel().getName() + "]: " + record.getMessage() + '\n';
        }
    }
}
