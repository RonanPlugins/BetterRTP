package me.SuperRonanCraft.BetterRTP.references.web;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Updater {

    public static String updatedVersion = BetterRTP.getInstance().getDescription().getVersion();

    public Updater(BetterRTP pl) {
        AsyncHandler.async(() -> {
            try {
                URLConnection con = new URL(getUrl() + project()).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                updatedVersion = reader.readLine();
            } catch (Exception ex) {
                Bukkit.getConsoleSender().sendMessage("[BetterRTP] Failed to check for an update on spigot");
                updatedVersion = pl.getDescription().getVersion();
            }
        });
    }

    private String getUrl() {
        return "https://api.spigotmc.org/legacy/update.php?resource=";
    }

    private String project() {
        return "36081";
    }
}
