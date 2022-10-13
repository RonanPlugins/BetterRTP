package me.SuperRonanCraft.BetterRTP.player.rtp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.packets.WrapperPlayServerNamedSoundEffect;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RTPSounds {

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

    void playTeleport(Player p) {
        if (!enabled)
            return;
        if (soundTeleport != null) {
            playSound(p.getLocation(), p, soundTeleport);
            //p.playSound(p.getLocation(), soundTeleport, 1F, 1F);
        }
    }

    void playDelay(Player p) {
        if (!enabled) return;
        if (soundDelay != null) {
            playSound(p.getLocation(), p, soundDelay);
            //p.playSound(p.getLocation(), soundDelay, 1F, 1F);
        }
    }

    void playSound(Location loc, Player p, String sound) {
        if (BetterRTP.getInstance().getSettings().isProtocolLibSounds()) {
            try {
                ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                WrapperPlayServerNamedSoundEffect packet = new WrapperPlayServerNamedSoundEffect(pm.createPacket(PacketType.Play.Server.NAMED_SOUND_EFFECT));
                packet.setSoundName(sound);
                packet.setEffectPositionX(loc.getBlockX());
                packet.setEffectPositionY(loc.getBlockY());
                packet.setEffectPositionZ(loc.getBlockZ());
                packet.sendPacket(p);
            } catch (NoClassDefFoundError | Exception e) {
                BetterRTP.getInstance().getLogger().severe("ProtocolLib Sounds is enabled in the effects.yml file, but no ProtocolLib plugin was found!");
                p.playSound(p.getLocation(), getSound(sound), 1F, 1F);
            }
        } else
            p.playSound(p.getLocation(), getSound(sound), 1F, 1F);
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
