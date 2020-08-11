package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RTPTitles {

    private boolean enabled;
    private String
            titleTeleport,
            titleDelay,
            titleCancel,
            titleLoading,
            subTeleport,
            subDelay,
            subCancel,
            subLoading;
    private boolean //Disable default messages in chat
            showMsgTeleport,
            showMsgDelay,
            showMsgCancel,
            showMsgLoading;

    void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.EFFECTS;
        enabled = config.getBoolean("Titles.Enabled");
        if (enabled) {
            //Titles
            titleTeleport = config.getString("Titles.Teleport.Title");
            titleDelay = config.getString("Titles.Delay.Title");
            titleCancel = config.getString("Titles.Cancelled.Title");
            titleLoading = config.getString("Titles.Loading.Title");
            //Sub titles
            subTeleport = config.getString("Titles.Teleport.Subtitle");
            subDelay = config.getString("Titles.Delay.Subtitle");
            subCancel = config.getString("Titles.Cancelled.Subtitle");
            subLoading = config.getString("Titles.Loading.Subtitle");
            //Messages
            showMsgTeleport = config.getBoolean("Titles.Teleport.SendMessage");
            showMsgDelay = config.getBoolean("Titles.Delay.SendMessage");
            showMsgCancel = config.getBoolean("Titles.Cancelled.SendMessage");
            showMsgLoading = config.getBoolean("Titles.Loading.SendMessage");
        }
    }

    void showTeleport(Player p, Location loc, int attempts) {
        if (!enabled) return;
        String title = getPlaceholders(titleTeleport, p, loc, attempts, 0);
        String sub = getPlaceholders(subTeleport, p, loc, attempts, 0);
        show(p, title, sub);
    }

    void showDelay(Player p, Location loc, int delay) {
        if (!enabled) return;
        String title = getPlaceholders(titleDelay, p, loc, 0, delay);
        String sub = getPlaceholders(subDelay, p, loc, 0, delay);
        show(p, title, sub);
    }

    void showCancelled(Player p, Location loc) {
        if (!enabled) return;
        String title = getPlaceholders(titleCancel, p, loc, 0, 0);
        String sub = getPlaceholders(subCancel, p, loc, 0, 0);
        show(p, title, sub);
    }

    void showLoading(Player p, Location loc) {
        if (!enabled) return;
        String title = getPlaceholders(titleLoading, p, loc, 0, 0);
        String sub = getPlaceholders(subLoading, p, loc, 0, 0);
        show(p, title, sub);
    }

    boolean sendMsgTeleport() {
        return !enabled || showMsgTeleport;
    }

    boolean sendMsgDelay() {
        return !enabled || showMsgDelay;
    }

    boolean sendMsgCancelled() {
        return !enabled || showMsgCancel;
    }

    boolean sendMsgLoading() {
        return !enabled || showMsgLoading;
    }

    private String getPlaceholders(String str, Player p, Location loc, int attempts, int delay) {
        return str.replace("%player%", p.getName())
                .replace("%x%", String.valueOf(loc.getBlockX()))
                .replace("%y%", String.valueOf(loc.getBlockY()))
                .replace("%z%", String.valueOf(loc.getBlockZ()))
                .replace("%attempts%", String.valueOf(attempts))
                .replace("%time%", String.valueOf(delay));
    }

    private void show(Player p, String title, String sub) {
        // int fadeIn = getPl().text.getFadeIn();
        // int stay = text.getStay();
        // int fadeOut = text.getFadeOut();
        title = Main.getInstance().getText().color(title);
        sub = Main.getInstance().getText().color(sub);
        p.sendTitle(title, sub);
        // player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }
}
