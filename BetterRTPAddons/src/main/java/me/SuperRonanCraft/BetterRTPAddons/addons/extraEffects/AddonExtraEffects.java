package me.SuperRonanCraft.BetterRTPAddons.addons.extraEffects;

import me.SuperRonanCraft.BetterRTPAddons.Addon;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class AddonExtraEffects implements Addon {

    private final String name = "ExtraEffects";
    private final List<Effects> effects = new ArrayList<>(); //List of enabled effects

    @Override
    public boolean isEnabled() {
        return getFile(Files.FILETYPE.CONFIG).getBoolean(name + ".Enabled");
    }

    @Override
    public void load() {
        this.effects.clear();
        for (Effects effect : Effects.values())
            if (effect.isEnabled()) {
                effect.load();
                this.effects.add(effect);
            }
    }

    @Override
    public void unload() {
        for (Effects effect : this.effects)
            effect.unload();
        this.effects.clear();
    }

    enum Effects {
        SKYHIGH(new ExtraEffectsEffect_Skyhigh());

        private final ExtraEffectsEffect effect;

        Effects(ExtraEffectsEffect effect) {
            this.effect = effect;
        }

        boolean isEnabled() {
            return effect.isEnabled();
        }

        void load() {
            effect.load();
        }

        void unload() {
            effect.unload();
        }
    }
}
