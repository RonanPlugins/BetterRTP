package me.SuperRonanCraft.BetterRTPAddons.addons.extraEffects;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportEvent;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

//Teleport the player VERY high into the sky
public class ExtraEffectsEffect_Skyhigh implements ExtraEffectsEffect, Listener {

    private int offset;

    @Override
    public void load() {
        Files.FILETYPE file = Main.getInstance().getFiles().getType(Files.FILETYPE.CONFIG);
        this.offset = file.getInt("ExtraEffects.YOffset.Offset");
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public boolean isEnabled() {
        return Main.getInstance().getFiles().getType(Files.FILETYPE.CONFIG).getBoolean("ExtraEffects.YOffset.Enabled");
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    void tpEvent(RTP_TeleportEvent e) {
        e.changeLocation(e.getLocation().add(0, offset, 0));
    }
}
