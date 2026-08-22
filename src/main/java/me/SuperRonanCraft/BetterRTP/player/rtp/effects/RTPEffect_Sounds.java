package me.SuperRonanCraft.BetterRTP.player.rtp.effects;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class RTPEffect_Sounds {

    private boolean enabled;
    private String soundTeleport, soundDelay;

    void load() {
        FileOther.FILETYPE config = FileOther.FILETYPE.EFFECTS;
        enabled = config.getBoolean("Sounds.Enabled");
        if (enabled) {
            soundTeleport = config.getString("Sounds.Success");
            soundDelay = config.getString("Sounds.Delay");
        }
    }

    public void playTeleport(Player p) {
        if (!enabled)
            return;
        if (soundTeleport != null) {
            playSound(p.getLocation(), p, soundTeleport);
        }
    }

    public void playDelay(Player p) {
        if (!enabled) return;
        if (soundDelay != null) {
            playSound(p.getLocation(), p, soundDelay);
        }
    }

    void playSound(Location loc, Player p, String sound) {
        //Prefer the enum lookup (handles legacy config names like 'entity_tnt_primed');
        //fall back to the raw sound key for custom resource-pack sounds.
        Sound enumSound = getSound(sound);
        if (enumSound != null)
            p.playSound(loc, enumSound, SoundCategory.MASTER, 1F, 1F);
        else
            p.playSound(loc, sound, SoundCategory.MASTER, 1F, 1F);
    }

    private Sound getSound(String sound) {
        try {
            return Sound.valueOf(sound.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
