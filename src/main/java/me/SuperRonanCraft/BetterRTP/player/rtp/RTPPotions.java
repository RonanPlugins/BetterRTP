package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RTPPotions { //Potions AND Invincibility

    private boolean potionEnabled;
    private final HashMap<PotionEffectType, Integer[]> potionEffects = new HashMap<>();
    private boolean invincibleEnabled;
    private int invincibleTime;

    void load() {
        potionEffects.clear();
        FileOther.FILETYPE config = FileOther.FILETYPE.EFFECTS;
        //Invincible
        invincibleEnabled = config.getBoolean("Invincible.Enabled");
        if (invincibleEnabled)
            invincibleTime = config.getInt("Invincible.Seconds");

        //Potions
        potionEnabled = config.getBoolean("Potions.Enabled");
        if (potionEnabled) {
            List<String> list = config.getStringList("Potions.Types");
            for (String p : list) {
                String[] ary = p.replaceAll(" ", "").split(":");
                String type = ary[0].trim();
                PotionEffectType effect = PotionEffectType.getByName(type);
                if (effect != null) {
                    try {
                        int duration = ary.length >= 2 ? Integer.parseInt(ary[1]) : 60;
                        int amplifier = ary.length >= 3 ? Integer.parseInt(ary[2]) : 1;
                        potionEffects.put(effect, new Integer[] {duration, amplifier});
                    } catch (NumberFormatException e) {
                        BetterRTP.getInstance().getLogger().info("The potion duration or amplifier `" + ary[1] + "` is not an integer. Effect was removed!");
                    }
                } else
                    BetterRTP.getInstance().getLogger().info("The potion effect `" + type + "` does not exist! " +
                            "Please fix or remove this potion effect! Try '/rtp info potion_effects' to get a list of valid effects!");
            }
        }
    }

    void giveEffects(Player p) {
        if (invincibleEnabled)
            p.setNoDamageTicks(invincibleTime * 20);
        if (potionEnabled) {
            List<PotionEffect> effects = new ArrayList<>();
            for (PotionEffectType e : potionEffects.keySet()) {
                Integer[] mods = potionEffects.get(e);
                int duration = mods[0];
                int amplifier = mods[1];
                effects.add(new PotionEffect(e, duration, amplifier, false, false));
            }
            p.addPotionEffects(effects);
        }
    }

}
