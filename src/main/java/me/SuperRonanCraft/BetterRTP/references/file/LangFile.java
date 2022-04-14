package me.SuperRonanCraft.BetterRTP.references.file;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class LangFile {
    private YamlConfiguration config = new YamlConfiguration();

    String getString(String path) {
        if (config.isString(path))
            return config.getString(path);
        return "SOMETHING WENT WRONG";
    }

    @SuppressWarnings("all")
    public List<String> getStringList(String path) {
        if (config.isList(path))
            return config.getStringList(path);
        return Arrays.asList("SOMETHING WENT WRONG!");
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @SuppressWarnings("all")
    public void load() {
        generateDefaults();
        String fileName = "lang" + File.separator + getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG).getString("Language-File");
        File file = new File(getPl().getDataFolder(), fileName);
        if (!file.exists()) {
            fileName = "lang" + File.separator + defaultLangs[0]; //Default to english
            file = new File(getPl().getDataFolder(), fileName);
        }
        try {
            config.load(file);
            InputStream in = BetterRTP.getInstance().getResource(fileName);
            if (in == null)
                in = getPl().getResource(fileName.replace(File.separator, "/"));
            if (in != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in)));
                config.options().copyDefaults(true);
                in.close();
            }
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String[] defaultLangs = {
            "chs.yml", //Chinese Simplified (OasisAkari)
            "cht.yml", //Chinese (OasisAkari & kamiya10)
            "cs.yml", //Czech (Lewisparkle)
            "da.yml", //Danish (Janbchr)
            "nl.yml", //Dutch (QuestalNetwork) (GeleVla)
            "en.yml",
            "es.yml", //Spanish (emgv)
            "fr.yml", //French (At0micA55 & Mrflo67)
            "it.yml", //Italian (iVillager)
            "ja.yml", //Japanese (ViaSnake)
            "no.yml", //Norwegian (Fraithor & Janbchr)
            "pl.yml", //Polish (Farum & TeksuSiK)
            "ro.yml", //Romanian (GamingXBlood)
            "ru.yml", //Russian (Logan)
    };

    private void generateDefaults() {
        //Generate all language files
        for (String yaml : defaultLangs) {
            generateDefaultConfig(yaml, yaml); //Generate its own defaults
            if (!yaml.equals(defaultLangs[0]))
                generateDefaultConfig(yaml, defaultLangs[0]); //Generate the english defaults (incase)
        }
    }

    private void generateDefaultConfig(String fName, String fNameDef /*Name of file to generate defaults*/) {
        String fileName = "lang" + File.separator + fName;
        File file = new File(getPl().getDataFolder(), fileName);
        if (!file.exists())
            getPl().saveResource(fileName, false);
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            String fileNameDef = "lang" + File.separator + fNameDef;
            InputStream in = BetterRTP.getInstance().getResource(fileNameDef);
            if (in == null)
                in = getPl().getResource(fileNameDef.replace(File.separator, "/"));
            if (in != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in)));
                config.options().copyDefaults(true);
                in.close();
            }
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
