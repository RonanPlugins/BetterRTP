package me.SuperRonanCraft.BetterRTP.references.file;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileOther {

    List<FILETYPE> types = new ArrayList<>();

    void load() {
        types.clear();
        for (FILETYPE type : FILETYPE.values()) {
            type.load();
            types.add(type);
        }
    }

    public enum FILETYPE implements FileData {
        CONFIG("config"),
        ECO("economy"),
        SIGNS("signs"),
        EFFECTS("effects"),
        LOCATIONS("locations"),
        PLACEHOLDERS("placeholders")
        ;

        private final String fileName;
        private final YamlConfiguration config = new YamlConfiguration();
        private final File file;

        FILETYPE(String str) {
            this.fileName = str + ".yml";
            this.file = new File(plugin().getDataFolder(), fileName);
        }

        @Override
        public Plugin plugin() {
            return BetterRTP.getInstance();
        }

        //PUBLIC
        @Override
        public YamlConfiguration getConfig() {
            return config;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String fileName() {
            return fileName;
        }
    }
}
