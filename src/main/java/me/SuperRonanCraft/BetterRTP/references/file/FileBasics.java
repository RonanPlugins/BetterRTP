package me.SuperRonanCraft.BetterRTP.references.file;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBasics {

    List<FILETYPE> types = new ArrayList<>();

    void load() {
        types.clear();
        for (FILETYPE type : FILETYPE.values()) {
            type.load();
            types.add(type);
        }
    }

    public enum FILETYPE {
        CONFIG("config"), ECO("economy"), SIGNS("signs");

        private String fileName;
        private YamlConfiguration config = new YamlConfiguration();

        FILETYPE(String str) {
            this.fileName = str + ".yml";
        }

        //PUBLIC
        public String getString(String path) {
            if (config.isString(path))
                return config.getString(path);
            return "SOMETHING WENT WRONG";
        }

        public boolean getBoolean(String path) {
            return config.getBoolean(path);
        }

        public int getInt(String path) {
            return config.getInt(path);
        }

        @SuppressWarnings("all")
        public List<String> getStringList(String path) {
            if (config.isList(path))
                return config.getStringList(path);
            return new ArrayList<>();
        }

        public ConfigurationSection getConfigurationSection(String path) {
            return config.getConfigurationSection(path);
        }

        public boolean isString(String path) {
            return config.isString(path);
        }

        public boolean isList(String path) {
            return config.isList(path);
        }

        public List<Map<?, ?>> getMapList(String path) {
            return config.getMapList(path);
        }

        public YamlConfiguration getFile() {
            return config;
        }

        //PROCCESSING
        private void load() {
            Main pl = Main.getInstance();
            File file = new File(pl.getDataFolder(), fileName);
            if (!file.exists())
                pl.saveResource(fileName, false);
            try {
                config.load(file);
                final InputStream defConfigStream = Main.getInstance().getResource(fileName);
                if (defConfigStream != null) {
                    config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream)));
                    config.options().copyDefaults(true);
                }
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
