package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTPAddons.addons.flashback.AddonFlashback;
import me.SuperRonanCraft.BetterRTPAddons.addons.interfaces.AddonInterface;
import me.SuperRonanCraft.BetterRTPAddons.addons.logger.AddonLogger;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AddonsHandler {

    List<Addons> addons = new ArrayList<>();
    AddonsCommand cmd = new AddonsCommand();

    public void load() {
        unload();
        //int addonCount = 0;
        for (Addons addon : Addons.values())
            if (addon.isEnabled()) {
                addon.load();
                addons.add(addon);
                //addonCount++;
            }
        //Main.getInstance().getLogger().log(Level.INFO, addonCount + "/" + Addons.values().length + " addons were enabled!");
        BetterRTP.getInstance().getCmd().registerCommand(cmd, false);
    }

    public void unload() {
        for (Addons addon : addons)
            addon.disable();
        addons.clear();
    }

    enum Addons {
        LOGGER(new AddonLogger()),
        FLASH_BACK(new AddonFlashback()),
        PORTALS(new AddonPortals()),
        //INTERFACES(new AddonInterface())
        ;

        Addon addon;

        Addons(Addon addon) {
            this.addon = addon;
        }

        boolean isEnabled() {
            return addon.isEnabled();
        }

        void load() {
            addon.load();
            //Main.getInstance().getLogger().log(Level.INFO, "Addon " + this.name() + " has been enabled!");
        }

        void disable() {
            addon.unload();
        }
    }

}
