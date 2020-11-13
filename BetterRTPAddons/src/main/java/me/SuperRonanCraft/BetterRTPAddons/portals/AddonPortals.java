package me.SuperRonanCraft.BetterRTPAddons.portals;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.flashback.FlashbackDatabase;
import org.bukkit.event.EventHandler;

//With a gui, a player
public class AddonPortals implements Addon {

    public PortalsMessages msgs = new PortalsMessages();
    private final PortalsCommand cmd = new PortalsCommand(this);
    private final PortalsDatabase database = new PortalsDatabase();;

    public boolean isEnabled() {
        return Files.FILETYPE.PORTALS.getBoolean("Enabled");
    }

    @Override
    public void load() {
        BetterRTP.getInstance().getCmd().registerCommand(cmd, false);
        this.database.load(PortalsDatabase.Columns.values());
    }

    @Override
    public void unload() {

    }

}
