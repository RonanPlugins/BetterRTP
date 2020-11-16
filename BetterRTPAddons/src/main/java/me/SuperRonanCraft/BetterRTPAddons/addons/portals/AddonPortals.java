package me.SuperRonanCraft.BetterRTPAddons.addons.portals;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Files;

//Create portals for rtp'ing
public class AddonPortals implements Addon {

    public PortalsMessages msgs = new PortalsMessages();
    private final PortalsCommand cmd = new PortalsCommand(this);
    private final PortalsDatabase database = new PortalsDatabase();;

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

    }

}
