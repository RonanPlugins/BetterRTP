package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RTPEffects {

    private boolean potionEnabled;
    private final HashMap<PotionEffectType, Integer> potionEffects = new HashMap<>();
    private boolean invincibleEnabled;
    private int invincibleTime;

    void load() {
        potionEffects.clear();
        FileBasics.FILETYPE config = FileBasics.FILETYPE.CONFIG;
        String pre = "Settings.Effects.";
        potionEnabled = config.getBoolean(pre + "Potions.Enabled");
        invincibleEnabled = config.getBoolean(pre + "Invincible.Enabled");
        if (invincibleEnabled)
            invincibleTime = config.getInt(pre + "Invincible.Seconds");
        if (potionEnabled) {
            List<String> list = config.getStringList(pre + "Potions.Types");
            for (String p : list) {
                String[] ary = p.split(":");
                String type = ary[0];
                PotionEffectType effect = PotionEffectType.getByName(type);
                if (effect != null) {
                    try {
                        int time = ary.length >= 2 ? Integer.parseInt(ary[1]) : 3;
                        potionEffects.put(effect, time);
                    } catch (NumberFormatException e) {
                        Main.getInstance().getLogger().info("The potion duration `" + ary[1] + "` is not an integer! Effect removed!");
                    }
                } else
                    Main.getInstance().getLogger().info("The potion effect `" + type + "` does not exist! " +
                            "Please fix or remove this potion effect! Try '/rtp info potion_effects'");
            }
        }
    }

    void giveEffects(Player p) {
        if (invincibleEnabled)
            p.setNoDamageTicks(invincibleTime);
        if (potionEnabled) {
            List<PotionEffect> effects = new ArrayList<>();
            for (PotionEffectType e : potionEffects.keySet())
                effects.add(new PotionEffect(e, potionEffects.get(e), 1, false, false));
            p.addPotionEffects(effects);
        }
    }

}
