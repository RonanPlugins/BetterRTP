package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RTPTitles {

    private boolean enabled;
    private String
            titleTeleport,
            titleDelay,
            subTeleport,
            subDelay;

    void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.EFFECTS;
        enabled = config.getBoolean("Titles.Enabled");
        if (enabled) {
            titleTeleport = config.getString("Titles.Teleport.Title");
            titleDelay = config.getString("Titles.Teleport.Title");
            subTeleport = config.getString("Titles.Teleport.Title");
            subDelay = config.getString("Titles.Teleport.Title");
        }
    }

    void showTeleport(Player p, Location loc, int attempts) {
        if (!enabled) return;
        String title = getPlaceholders(titleTeleport, p, loc, attempts);
        String subTitle = getPlaceholders(subTeleport, p, loc, attempts);
        show(p, title, subTitle);
    }

    void showDelay(Player p, Location loc) {
        if (!enabled) return;
        String title = getPlaceholders(titleDelay, p, loc, 0);
        String subTitle = getPlaceholders(subDelay, p, loc, 0);
        show(p, title, subTitle);
    }

    private String getPlaceholders(String str, Player p, Location loc, int attempts) {
        return str.replace("%player%", p.getName())
                .replace("%x%", String.valueOf(loc.getBlockX()))
                .replace("%y%", String.valueOf(loc.getBlockY()))
                .replace("%z%", String.valueOf(loc.getBlockZ()))
                .replace("%attempts%", String.valueOf(attempts));
    }

    private void show(Player p, String title, String sub) {
        // int fadeIn = getPl().text.getFadeIn();
        // int stay = text.getStay();
        // int fadeOut = text.getFadeOut();
        p.sendTitle(title, sub);
        // player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }
}
