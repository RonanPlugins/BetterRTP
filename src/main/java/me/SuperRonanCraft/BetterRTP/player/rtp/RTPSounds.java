package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RTPSounds {

    private boolean enabled;
    private Sound
            soundTeleport,
            soundDelay;

    void load() {
        FileBasics.FILETYPE config = FileBasics.FILETYPE.EFFECTS;
        enabled = config.getBoolean("Sounds.Enabled");
        if (enabled) {
            soundTeleport = getSound(config.getString("Sounds.Success"));
            soundDelay = getSound(config.getString("Sounds.Delay"));
        }
    }

    void playTeleport(Player p) {
        if (!enabled) return;
        if (soundTeleport != null)
            p.playSound(p.getLocation(), soundTeleport, 1F, 1F);
    }

    void playDelay(Player p) {
        if (!enabled) return;
        if (soundDelay != null)
            p.playSound(p.getLocation(), soundDelay, 1F, 1F);
    }

    private Sound getSound(String sound) {
        try {
            return Sound.valueOf(sound.toUpperCase());
        } catch (IllegalArgumentException e) {
            BetterRTP.getInstance().getLogger().info("The sound '" + sound + "' is invalid!");
            return null;
        }
    }
}
