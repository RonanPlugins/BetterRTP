package me.SuperRonanCraft.BetterRTPAddons.flashback;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

//When rtp'ing, a player will be teleported back to old location after a set amount of time
public class AddonFlashback implements Addon {

    private Long time;
    public FlashbackMessages msgs = new FlashbackMessages();
    public FlashbackDatabase database;
    List<FlashbackPlayer> players = new ArrayList<>();

    public boolean isEnabled() {
        return Files.FILETYPE.FLASHBACK.getBoolean("Enabled");
    }

    @Override
    public void load() {
        Files.FILETYPE file = Files.FILETYPE.FLASHBACK;
        this.time = file.getConfig().getLong("Time");
        this.database = new FlashbackDatabase(Main.getInstance());
        this.database.load();
    }

    @Override
    public void unload() {
        for (FlashbackPlayer fbp : players)
            fbp.cancel();
        players.clear();
    }

    @EventHandler
    void onTeleport(RTP_TeleportPostEvent e) {
        System.out.println("Player " + e.getPlayer().getName() + " was rtp'd!");
        players.add(new FlashbackPlayer(this, e.getPlayer(), e.getOldLocation(), time));
    }
}
