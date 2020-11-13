package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTPAddons.flashback.AddonFlashback;
import me.SuperRonanCraft.BetterRTPAddons.interfaces.AddonInterface;
import me.SuperRonanCraft.BetterRTPAddons.logger.AddonLogger;
import me.SuperRonanCraft.BetterRTPAddons.portals.AddonPortals;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AddonsHandler {

    List<Addons> addons = new ArrayList<>();
    AddonsCommand cmd = new AddonsCommand();

    public void load() {
        for (Addons addon : addons) {
            addon.disable();
        }
        addons.clear();
        for (Addons addon : Addons.values())
            if (addon.isEnabled()) {
                addon.load();
                addons.add(addon);
            } else {
                Main.getInstance().getLogger().log(Level.INFO, "Addon " + addon.name() + " was NOT enabled.");
            }
        BetterRTP.getInstance().getCmd().registerCommand(cmd, false);
    }

    enum Addons {
        FLASH_BACK(new AddonFlashback()),
        PORTALS(new AddonPortals()),
        INTERFACES(new AddonInterface()),
        LOGGER(new AddonLogger());

        Addon addon;

        Addons(Addon addon) {
            this.addon = addon;
        }

        boolean isEnabled() {
            return addon.isEnabled();
        }

        void load() {
            addon.load();
            Main.getInstance().getLogger().log(Level.INFO, "Addon " + this.name() + " has been enabled!");
        }

        void disable() {
            addon.unload();
        }
    }

}
