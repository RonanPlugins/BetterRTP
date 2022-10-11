package me.SuperRonanCraft.BetterRTP.references.file;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface FileData {

    YamlConfiguration getConfig();

    File getFile();

    String fileName();

    Plugin plugin();

    default String getString(String path) {
        if (getConfig().isString(path))
            return getConfig().getString(path);
        return "SOMETHING WENT WRONG";
    }

    default boolean exists(String path) {
        return getConfig().contains(path);
    }

    default boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    default int getInt(String path) {
        return getConfig().getInt(path);
    }

    default long getLong(String path) {
        return getConfig().getLong(path);
    }

    default List<String> getStringList(String path) {
        if (getConfig().isList(path))
            return getConfig().getStringList(path);
        return new ArrayList<>();
    }

    //Can be configured as a String OR List
    default List<String> getList(String path) {
        List<String> list = new ArrayList<>();
        if (getConfig().isList(path)) list.addAll(getStringList(path));
        else if (getConfig().isString(path)) list.add(getString(path));
        else return List.of("&7The path &e" + path + " &7was not configured correctly!");
        return list;
    }

    default ConfigurationSection getConfigurationSection(String path) {
        return getConfig().getConfigurationSection(path);
    }

    default boolean isString(String path) {
        return getConfig().isString(path);
    }

    default boolean isList(String path) {
        return getConfig().isList(path);
    }

    default List<Map<?, ?>> getMapList(String path) {
        return getConfig().getMapList(path);
    }

    default void setValue(String path, Object value) {
        getConfig().set(path, value);
    }

    //PROCCESSING
    default void load() {
        YamlConfiguration config = getConfig();
        File file = getFile();
        if (!getFile().exists()) {
            plugin().saveResource(fileName(), false);
            try {
                config.load(file);
            } catch (Exception e) {
                plugin().getLogger().info("File " + fileName() + " was unable to load!");
                e.printStackTrace();
            }
        } else {
            try {
                config.load(file);
                final InputStream in = plugin().getResource(fileName().replace(File.separator, "/"));
                if (in != null && in.available() > 0) {
                    config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in)));
                    config.options().copyDefaults(true);
                    in.close();
                } else {
                    System.out.println("Input file was nulled " + fileName());
                }
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    default void save() {
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
