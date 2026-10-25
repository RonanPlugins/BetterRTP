package me.SuperRonanCraft.BetterRTP.player.rtp.effects;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.Locale;

public class RTPEffect_Sounds {
    private boolean enabled;
    private String soundTeleport, soundDelay;

    void load() {
        FileOther.FILETYPE config = FileOther.FILETYPE.EFFECTS;
        enabled = config.getBoolean("Sounds.Enabled");
        if (enabled) {
            soundTeleport = resolveSound(config.getString("Sounds.Success"));
            soundDelay = resolveSound(config.getString("Sounds.Delay"));
        }
    }

    public void playTeleport(Player p) {
        if (enabled && soundTeleport != null)
            playSound(p.getLocation(), p, soundTeleport);
    }

    public void playDelay(Player p) {
        if (enabled && soundDelay != null)
            playSound(p.getLocation(), p, soundDelay);
    }

    void playSound(Location loc, Player p, String sound) {
        // Bukkit handles packet changes and resource-pack sounds across versions.
        p.playSound(loc, sound, 1F, 1F);
    }

    static String resolveSound(String configured) {
        if (configured == null || configured.isBlank())
            return null;
        String name = configured.trim();
        for (Sound sound : Registry.SOUNDS) {
            String key = sound.getKey().getKey();
            if (key.replace('.', '_').equalsIgnoreCase(name))
                return sound.getKey().toString();
        }
        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ROOT));
        if (key != null && (name.contains(":") || name.contains(".")))
            return key.toString();
        BetterRTP.getInstance().getLogger().warning("Invalid sound: " + configured + "; skipping this sound.");
        return null;
    }
}
