package me.SuperRonanCraft.BetterRTP.references.file;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileLanguage implements FileData {
    private final YamlConfiguration config = new YamlConfiguration();

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public String fileName() {
        return null;
    }

    @Override
    public Plugin plugin() {
        return BetterRTP.getInstance();
    }

    @Override
    public void load() {
        generateDefaults();
        String fileName = "lang" + File.separator + FileOther.FILETYPE.CONFIG.getString("Language-File");
        File file = new File(plugin().getDataFolder(), fileName);
        if (!file.exists()) {
            fileName = "lang" + File.separator + defaultLangs[0]; //Default to english
            file = new File(plugin().getDataFolder(), fileName);
        }
        try {
            config.load(file);
            InputStream in = plugin().getResource(fileName);
            if (in == null)
                in = plugin().getResource(fileName.replace(File.separator, "/"));
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
            "de.yml", //German (IBimsDaNico#8690)
            "en.yml",
            "es.yml", //Spanish (emgv)
            "fr.yml", //French (At0micA55 & Mrflo67)
            "he.yml", //Hebrew (thefourcraft)
            "it.yml", //Italian (iVillager)
            "ja.yml", //Japanese (ViaSnake)
            "nl.yml", //Dutch (QuestalNetwork) (GeleVla)
            "no.yml", //Norwegian (Fraithor & Janbchr)
            "pl.yml", //Polish (Farum & TeksuSiK)
            "ro.yml", //Romanian (GamingXBlood)
            "ru.yml", //Russian (Logan)
            "vi.yml", //Vietnamese (VoChiDanh#0862)
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
        File file = new File(plugin().getDataFolder(), fileName);
        if (!file.exists())
            plugin().saveResource(fileName, false);
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            String fileNameDef = "lang" + File.separator + fNameDef;
            InputStream in = plugin().getResource(fileNameDef);
            if (in == null)
                in = plugin().getResource(fileNameDef.replace(File.separator, "/"));
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
}
