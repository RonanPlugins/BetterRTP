package me.SuperRonanCraft.BetterRTPAddons.flashBack;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import org.bukkit.event.EventHandler;

public class AddonFlashback implements Addon {

    private Long time;
    public MessagesFlashback msgs = new MessagesFlashback();

    public boolean isEnabled() {
        return Files.FILETYPE.EFFECTS.getBoolean("Enabled");
    }

    @Override
    public void load() {
        Files.FILETYPE file = Files.FILETYPE.EFFECTS;
        this.time = file.getConfig().getLong("Time");
    }

    @EventHandler
    void onTeleport(RTP_TeleportPostEvent e) {
        System.out.println("Player " + e.getPlayer().getName() + " was rtp'd!");
        new PlayerFlashback(this, e.getPlayer(), e.getOldLocation(), 20L * time);
    }
}
