package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Create portals for rtp'ing
public class AddonPortals implements Addon {

    public PortalsMessages msgs = new PortalsMessages();
    private final PortalsCommand cmd = new PortalsCommand(this);
    private final PortalsDatabase database = new PortalsDatabase();
    private final PortalsCache portalsCache = new PortalsCache();

    public boolean isEnabled() {
        return getFile(Files.FILETYPE.PORTALS).getBoolean("Enabled");
    }

    @Override
    public void load() {
        BetterRTP.getInstance().getCmd().registerCommand(cmd, false);
        this.database.load(PortalsDatabase.Columns.values());
    }

    @Override
    public void unload() {
        this.portalsCache.unload();
    }

    public PortalsCache getPortals() {
        return portalsCache;
    }
}
