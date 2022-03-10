package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Command;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds.PortalsCommand;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsCache;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;

//Create portals for rtp'ing
public class AddonPortals implements Addon {

    private final String name = "Portals";
    public PortalsMessages msgs = new PortalsMessages();
    private final PortalsCommand cmd = new PortalsCommand(this);
    private final PortalsDatabase database = new PortalsDatabase();
    private final PortalsCache portalsCache = new PortalsCache(this);
    private final PortalsEvents events = new PortalsEvents(this);

    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        HelperRTP_Command.registerCommand(cmd, false);
        database.load(PortalsDatabase.Columns.values());
        events.load();
        portalsCache.load();
    }

    @Override
    public void unload() {
        portalsCache.unload();
        events.unregister();
    }

    @Override
    public RTPCommand getCmd() {
        return cmd;
    }

    public PortalsCache getPortals() {
        return portalsCache;
    }

    public PortalsDatabase getDatabase() {
        return database;
    }
}
