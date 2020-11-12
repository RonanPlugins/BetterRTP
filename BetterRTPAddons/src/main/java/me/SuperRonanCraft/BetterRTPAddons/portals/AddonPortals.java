package me.SuperRonanCraft.BetterRTPAddons.portals;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import org.bukkit.event.EventHandler;

//With a gui, a player
public class AddonPortals implements Addon {

    public PortalsMessages msgs = new PortalsMessages();
    private PortalsDatabase database;

    public boolean isEnabled() {
        return Files.FILETYPE.PORTALS.getBoolean("Enabled");
    }

    @Override
    public void load() {
        this.database = new PortalsDatabase(Main.getInstance());
        BetterRTP.getInstance().getCmd().registerCommand(new PortalsCommand(), false);
    }

    @Override
    public void unload() {

    }

}
