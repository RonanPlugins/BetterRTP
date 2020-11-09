package me.SuperRonanCraft.BetterRTP.references.file;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class EcoFile {
    private YamlConfiguration lang = new YamlConfiguration();
    private File langFile;

    void load() {
        Main pl = Main.getInstance();
        langFile = new File(pl.getDataFolder(), "economy.yml");
        if (!langFile.exists())
            pl.saveResource("economy.yml", false);
        loadFile();
    }

    public String getString(String path) {
        if (lang.isString(path))
            return lang.getString(path);
        return "SOMETHING WENT WRONG";
    }

    @SuppressWarnings("all")
    public List<String> getStringList(String path) {
        if (lang.isList(path))
            return lang.getStringList(path);
        return Arrays.asList("SOMETHING WENT WRONG!");
    }

    public int getInt(String path) {
        return lang.getInt(path);
    }

    public boolean getBoolean(String path) {
        return lang.getBoolean(path);
    }


    private void loadFile() {
        try {
            lang.load(langFile);
            setDefaults();
            lang.save(langFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaults() {
        final InputStream defConfigStream = Main.getInstance().getResource("economy.yml");
        if (defConfigStream == null)
            return;
        lang.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream)));
        lang.options().copyDefaults(true);
    }
}

