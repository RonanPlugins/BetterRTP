package me.SuperRonanCraft.BetterRTPAddons.addons.logger;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdReload;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CommandEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class AddonLogger implements Addon, Listener {

    private final String name = "Logger";
    private String format;
    private boolean toConsole;
    Logger logger;
    FileHandler handler;
    ConsoleHandler consoleHandler_rtp, consoleHandler_main;

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        this.format = getFile(Files.FILETYPE.CONFIG).getString(name + ".Format");
        this.toConsole = getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".LogToConsole");
        try {
            File f = new File(Main.getInstance().getDataFolder() + File.separator + "log.txt");
            handler = new FileHandler(f.getPath(), true);
            handler.setFormatter(new MyFormatter(this));
            logger = Logger.getLogger(Main.getInstance().getName() + "-Log");
            logger.setUseParentHandlers(this.toConsole); //Disable logging to console
            logger.addHandler(handler);
            //Log copying
            consoleHandler_rtp = new MyConsole(this.logger, BetterRTP.getInstance().getName());
            BetterRTP.getInstance().getLogger().addHandler(consoleHandler_rtp);
            consoleHandler_main = new MyConsole(this.logger, Main.getInstance().getName());
            Main.getInstance().getLogger().addHandler(consoleHandler_main);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
        logger.removeHandler(handler);
        handler.close();
        BetterRTP.getInstance().getLogger().removeHandler(consoleHandler_rtp);
        Main.getInstance().getLogger().removeHandler(consoleHandler_main);
    }

    @EventHandler
    public void onCmd(RTP_CommandEvent e) {
        String _str = e.getSendi().getName() + " executed `/rtp " + e.getCmd().getName() + "` at " + getDate();
        Level lvl = Level.INFO;
        if (e.getCmd() instanceof CmdReload)
            lvl = Level.WARNING;
        log(_str, lvl);
    }

    @EventHandler
    public void onTeleport(RTP_TeleportPostEvent e) {
        String _str = e.getPlayer().getName() + " has teleported to " + e.getLocation().toString()
                + " in world " + e.getLocation().getWorld().getName()
                + " at" + getDate();
        log(_str, Level.INFO);
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat(this.format);
        return format.format(new Date());
    }

    private void log(String str, Level lvl) {
        logger.log(lvl, str);
    }

    //Make the logs come out readable
    static class MyFormatter extends Formatter {
        AddonLogger addon;

        MyFormatter(AddonLogger addon) {
            this.addon = addon;
        }

        @Override
        public String format(LogRecord record) {
            return addon.getDate() + " [" + record.getLevel().getName() + "]: " + record.getMessage() + '\n';
        }
    }

    //Copy one log to another log
    static class MyConsole extends ConsoleHandler {

        Level lvl;
        Logger logger;

        MyConsole(Logger logger, String name) {
            this.logger = logger;
            lvl = new MyLevel(name, Integer.MAX_VALUE);
        }

        @Override
        public void publish(LogRecord record) {
            logger.log(lvl, record.getMessage());
        }
    }

    static class MyLevel extends Level {

        protected MyLevel(String name, int value) {
            super(name, value);
        }
    }
}
