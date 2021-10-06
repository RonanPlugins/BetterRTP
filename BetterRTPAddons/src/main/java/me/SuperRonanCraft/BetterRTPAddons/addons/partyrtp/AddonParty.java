package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.cmds.PartyCommand;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.PortalsMessages;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;

import java.util.ArrayList;
import java.util.List;

public class AddonParty implements Addon {

    private static AddonParty instance;
    private final String name = "PartyRTP";
    private final PartyCommand cmd = new PartyCommand(this);
    public PortalsMessages msgs = new PortalsMessages();
    public List<PartyData> parties = new ArrayList<>();

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void unload() {

    }

    public static AddonParty getInstance() {
        return instance;
    }

    @Override
    public RTPCommand getCmd() {
        return null;
    }
}
