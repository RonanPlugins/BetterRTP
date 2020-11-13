package me.SuperRonanCraft.BetterRTPAddons.flashback;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

//When rtp'ing, a player will be teleported back to old location after a set amount of time
public class AddonFlashback implements Addon, Listener {

    private Long time;
    public final FlashbackMessages msgs = new FlashbackMessages();
    public final FlashbackDatabase database = new FlashbackDatabase();
    List<FlashbackPlayer> players = new ArrayList<>();

    public boolean isEnabled() {
        return Files.FILETYPE.FLASHBACK.getBoolean("Enabled");
    }

    @Override
    public void load() {
        Files.FILETYPE file = Files.FILETYPE.FLASHBACK;
        this.time = file.getConfig().getLong("Time");
        this.database.load(FlashbackDatabase.Columns.values());
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void unload() {
        for (FlashbackPlayer fbp : players)
            fbp.cancel();
        players.clear();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    void onTeleport(RTP_TeleportPostEvent e) {
        System.out.println("Player " + e.getPlayer().getName() + " was rtp'd!");
        players.add(new FlashbackPlayer(this, e.getPlayer(), e.getOldLocation(), time));
    }
}
