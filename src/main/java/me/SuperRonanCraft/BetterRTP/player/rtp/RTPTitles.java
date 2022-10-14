package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

public class RTPTitles {

    boolean enabled = false;
    private final HashMap<RTP_TITLE_TYPE, RTP_TITLE> titles = new HashMap<>();

    void load() {
        titles.clear();
        FileOther.FILETYPE config = FileOther.FILETYPE.EFFECTS;
        enabled = config.getBoolean("Titles.Enabled");
        if (enabled)
            for (RTP_TITLE_TYPE type : RTP_TITLE_TYPE.values())
                titles.put(type, new RTP_TITLE(type.path));
    }

    void showTitle(RTP_TITLE_TYPE type, Player p, Location loc, int attempts, int delay) {
        if (titles.containsKey(type)) {
            String title = getPlaceholders(titles.get(type).title, p, loc, attempts, delay);
            String sub = getPlaceholders(titles.get(type).subTitle, p, loc, attempts, delay);
            show(p, title, sub);
        }
    }

    boolean sendMsg(RTP_TITLE_TYPE type) {
        return titles.containsKey(type) && titles.get(type).send_message || !enabled;
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
        title = Message.color(title);
        sub = Message.color(sub);
        Message.smsTitle(p, Arrays.asList(title, sub));
        //p.sendTitle(title, sub);
        // player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    enum RTP_TITLE_TYPE {
        NODELAY("NoDelay"), TELEPORT("Teleport"), DELAY("Delay"), CANCEL("Cancelled"), LOADING("Loading"), FAILED("Failed");
        final String path;
        RTP_TITLE_TYPE(String path) {
            this.path = path;
        }
    }

    private static class RTP_TITLE {
        String title, subTitle;
        boolean send_message;

        RTP_TITLE(String path) {
            FileOther.FILETYPE config = FileOther.FILETYPE.EFFECTS;
            title = config.getString("Titles." + path + ".Title");
            subTitle = config.getString("Titles." + path + ".Subtitle");
            send_message = config.getBoolean("Titles." + path + ".SendMessage");
        }

    }
}
