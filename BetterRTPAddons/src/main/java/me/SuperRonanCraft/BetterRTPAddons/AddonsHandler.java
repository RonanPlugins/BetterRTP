package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTPAddons.flashback.AddonFlashback;
import me.SuperRonanCraft.BetterRTPAddons.interfaces.AddonInterface;
import me.SuperRonanCraft.BetterRTPAddons.portals.AddonPortals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AddonsHandler {

    List<Addons> addons = new ArrayList<>();

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
    }

    enum Addons {
        FLASH_BACK(new AddonFlashback()),
        PORTALS(new AddonPortals()),
        INTERFACES(new AddonInterface());

        Addon addon;

        Addons(Addon addon) {
            this.addon = addon;
        }

        boolean isEnabled() {
            return addon.isEnabled();
        }

        void load() {
            addon.register();
            addon.load();
            Main.getInstance().getLogger().log(Level.INFO, "Addon " + this.name() + " has been enabled!");
        }

        void disable() {
            addon.unregister();
        }
    }

}
