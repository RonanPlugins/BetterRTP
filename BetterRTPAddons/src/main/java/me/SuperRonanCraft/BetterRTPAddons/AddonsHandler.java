package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTPAddons.flashBack.AddonFlashback;

import java.util.ArrayList;
import java.util.HashMap;
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
                Main.getInstance().getLogger().log(Level.INFO, "Addon was not loaded " + addon.name());
            }
    }

    enum Addons {
        FLASH_BACK(new AddonFlashback());

        Addon addon;

        Addons(Addon addon) {
            this.addon = addon;
        }

        boolean isEnabled() {
            return addon.isEnabled();
        }

        void load() {
            Main.getInstance().getLogger().log(Level.INFO, "Addon loaded " + this.name());
            addon.register();
            addon.load();
        }

        void disable() {
            addon.disable();
        }
    }

}
