package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.database.DatabaseHandler;
import me.SuperRonanCraft.BetterRTP.references.helpers.FoliaHelper;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import me.SuperRonanCraft.BetterRTP.versions.FoliaHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitTask;

public class WorldLoad {

    BukkitTask loader;

    void load(WorldLoadEvent e) {
        //BetterRTP.getInstance().getLogger().info("NEW WORLD!");
        if (loader != null)
            loader.cancel();
        loader = AsyncHandler.syncLater(() -> {
            BetterRTP.debug("New world `" + e.getWorld().getName() + "` detected! Reloaded Databases!");
            BetterRTP.getInstance().getDatabaseHandler().load();
        }, 20L * 5);
    }
}
