package me.SuperRonanCraft.BetterRTPAddons.addons.magicStick;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.addons.magicStick.cmds.MagicStickCommand;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.inventory.ItemStack;

public class AddonMagicStick implements Addon {

    public MagicStickMessages msgs = new MagicStickMessages();
    MagicStickCommand cmd = new MagicStickCommand(this);
    public MagicStickEvents events = new MagicStickEvents();

    @Override
    public boolean isEnabled() {
        return Main.getInstance().getFiles().getType(Files.FILETYPE.CONFIG).getBoolean("MagicStick.Enabled");
    }

    @Override
    public void load() {
        BetterRTP.getInstance().getCmd().registerCommand(cmd, false);
        events.load();
    }

    @Override
    public void unload() {
        events.unload();
    }
}
