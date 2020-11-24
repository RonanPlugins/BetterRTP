package me.SuperRonanCraft.BetterRTPAddons.addons.extraEffects;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportEvent;
import me.SuperRonanCraft.BetterRTP.references.worlds.WORLD_TYPE;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

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
        if (e.getWorldType() == WORLD_TYPE.NETHER)
            return;
        e.changeLocation(e.getLocation().add(0, offset, 0));
        new PlayerFalling(e.getPlayer());
    }

    private static class PlayerFalling implements Listener {
        Player p;

        PlayerFalling(Player p) {
            this.p = p;
            Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        }

        @EventHandler
        void event(EntityDamageEvent e) {
            if (e.getEntityType() == EntityType.PLAYER && e.getEntity() == p) {
                e.setCancelled(true);
                unload();
            }
        }

        void unload() {
            HandlerList.unregisterAll(this);
        }
    }
}
