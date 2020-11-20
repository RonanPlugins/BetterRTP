package me.SuperRonanCraft.BetterRTPAddons.addons.flashback;

import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//When rtp'ing, a player will be teleported back to old location after a set amount of time
public class AddonFlashback implements Addon, Listener {

    private String name = "Flashback";

    private Long time;
    public final FlashbackMessages msgs = new FlashbackMessages();
    public final FlashbackDatabase database = new FlashbackDatabase();
    List<FlashbackPlayer> players = new ArrayList<>();
    HashMap<Long, String> warnings = new HashMap<>();

    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        Files.FILETYPE file = getFile(Files.FILETYPE.CONFIG);
        this.time = file.getConfig().getLong(name + ".Timer.Delay");
        this.database.load(FlashbackDatabase.Columns.values());

        warnings.clear();
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        List<Map<?, ?>> override_map = file.getConfig().getMapList(name + ".Timer.Warnings");
        for (Map<?, ?> m : override_map)
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                try {
                    Long secs = getLong(entry.getKey().toString());
                    warnings.put(secs, entry.getValue().toString());
                    Main.getInstance().getLogger().info("- Warnings: Time: '" + entry.getKey() + "' Message: '" + entry.getValue() + "' added");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("Warning value '" + entry.getKey().toString() + "' is " +
                            "invalid! Please make sure to format [- INTEGER: 'Message']");
                }
            }
    }

    private Long getLong(String str) throws NumberFormatException {
        return Long.valueOf(str);
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
        if (e.getType() != RTP_TYPE.ADDON_PORTAL)
            players.add(new FlashbackPlayer(this, e.getPlayer(), e.getOldLocation(), time));
    }
}
