package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Command;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp.cmds.PartyCommand;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class AddonParty implements Addon {

    private static AddonParty instance;
    private final String name = "Parties";
    private final PartyCommand cmd = new PartyCommand(this);
    public PartyMessages msgs = new PartyMessages();
    public List<PartyData> parties = new ArrayList<>();
    private final PartyHandler handler = new PartyHandler();

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        instance = this;
        HelperRTP_Command.registerCommand(cmd, false);
        PluginManager pm = BetterRTP.getInstance().getServer().getPluginManager();
        pm.registerEvents(handler, BetterRTP.getInstance());
    }

    @Override
    public void unload() {

    }

    public static AddonParty getInstance() {
        return instance;
    }

    @Override
    public RTPCommand getCmd() {
        return cmd;
    }
}
