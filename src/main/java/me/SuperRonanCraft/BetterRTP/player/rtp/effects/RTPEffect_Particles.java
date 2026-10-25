package me.SuperRonanCraft.BetterRTP.player.rtp.effects;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.Particle;
import java.util.Locale;
import java.util.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RTPEffect_Particles {

    private boolean enabled;
    private final List<Particle> effects = new ArrayList<>();
    private String shape;
    private final int precision = 16;

    //Some particles act very differently and might not care how they are shaped before animating, ex: EXPLOSION_NORMAL
    public static String[] shapeTypes = {
            "SCAN", //Body scan
            "EXPLODE", //Make an explosive entrance
            "TELEPORT" //Startrek type of portal
            };

    void load() {
        FileOther.FILETYPE config = getPl().getFiles().getType(FileOther.FILETYPE.EFFECTS);
        enabled = config.getBoolean("Particles.Enabled");
        if (!enabled) return;
        //Enabled? Load all this junk
        List<String> types;
        if (config.isList("Particles.Type"))
            types = config.getStringList("Particles.Type");
        else {
            types = new ArrayList<>();
            types.add(config.getString("Particles.Type"));
        }
        effects.clear();
        for (String type : types) {
            Particle effect = resolveParticle(type);
            if (effect != null && effect.getDataType() == Void.class)
                effects.add(effect);
            else
                getPl().getLogger().warning("Unsupported particle '" + type +
                        "' or particle requires extra data; skipping it. Use '/rtp info particles'.");
        }
        shape = config.getString("Particles.Shape").toUpperCase(Locale.ROOT);
        if (!Arrays.asList(shapeTypes).contains(shape)) {
            getPl().getLogger().severe("The particle shape '" + shape + "' doesn't exist! Default particle shape enabled...");
            getPl().getLogger().severe("Try using '/rtp info shapes' to get a list of shapes, or: " + Arrays.asList(shapeTypes));
            shape = shapeTypes[0];
        }
    }

    public void display(Player p) {
        if (!enabled) return;
        AsyncHandler.syncAtEntity(p, () -> {
            try { //Incase the library errors out
                switch (shape) {
                    case "TELEPORT":
                        partTeleport(p);
                        break;
                    case "EXPLODE":
                        partExplosion(p);
                        break;
                    default: //Super redundant, but... just future proofing
                    case "SCAN":
                        partScan(p);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void partScan(Player p) { //Particles with negative velocity
        Location loc = p.getLocation().add(new Vector(0, 1.75, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index);
            for (Particle effect : effects) {
                p.spawnParticle(effect, loc.clone().add(vec), 0, 0, -0.125, 0, .15);
            }
        }
    }

    private void partTeleport(Player p) { //Static particles in a shape
        Location loc = p.getLocation();
        for (float y = 2.5f; y > 0; y -= .25f)
            for (int index = 1; index < precision; index++) {
                //double yran = ran.nextGaussian() * pHeight;
                Vector vec = getVecCircle(index).add(new Vector(0, y, 0));
                for (Particle effect : effects) {
                    p.spawnParticle(effect, loc.clone().add(vec), 1);
                }
            }
    }

    private void partExplosion(Player p) { //Particles with a shape and forward velocity
        Location loc = p.getLocation().add(new Vector(0, 1, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index);
            for (Particle effect : effects) {
                p.spawnParticle(effect, loc.clone().add(vec), 0, vec.getX(), vec.getY(), vec.getZ(), 1.5);
            }
        }
    }

    private Vector getVecCircle(int index) {
        double p1 = (index * Math.PI) / (precision / 2);
        double p2 = (index - 1) * Math.PI / (precision / 2);
        //Positions
        int radius = 3;
        double x1 = Math.cos(p1) * radius;
        double x2 = Math.cos(p2) * radius;
        double z1 = Math.sin(p1) * radius;
        double z2 = Math.sin(p2) * radius;
        return new Vector(x2 - x1, 0, z2 - z1);
    }

    // Bukkit renamed these particles after 1.20; accept either spelling.
    private static final Map<String, String> PARTICLE_NAMES = Map.ofEntries(
        Map.entry("EXPLOSION_NORMAL", "POOF"), Map.entry("EXPLOSION_LARGE", "EXPLOSION"),
        Map.entry("EXPLOSION_HUGE", "EXPLOSION_EMITTER"), Map.entry("FIREWORKS_SPARK", "FIREWORK"),
        Map.entry("WATER_BUBBLE", "BUBBLE"), Map.entry("WATER_SPLASH", "SPLASH"),
        Map.entry("WATER_WAKE", "FISHING"), Map.entry("SUSPENDED", "UNDERWATER"),
        Map.entry("SUSPENDED_DEPTH", "UNDERWATER"), Map.entry("CRIT_MAGIC", "ENCHANTED_HIT"),
        Map.entry("SMOKE_NORMAL", "SMOKE"), Map.entry("SMOKE_LARGE", "LARGE_SMOKE"),
        Map.entry("SPELL", "EFFECT"), Map.entry("SPELL_INSTANT", "INSTANT_EFFECT"),
        Map.entry("SPELL_MOB", "ENTITY_EFFECT"), Map.entry("SPELL_MOB_AMBIENT", "ENTITY_EFFECT"),
        Map.entry("SPELL_WITCH", "WITCH"), Map.entry("DRIP_WATER", "DRIPPING_WATER"),
        Map.entry("DRIP_LAVA", "DRIPPING_LAVA"), Map.entry("VILLAGER_ANGRY", "ANGRY_VILLAGER"),
        Map.entry("VILLAGER_HAPPY", "HAPPY_VILLAGER"), Map.entry("TOWN_AURA", "MYCELIUM"),
        Map.entry("ENCHANTMENT_TABLE", "ENCHANT"), Map.entry("REDSTONE", "DUST"),
        Map.entry("SNOWBALL", "ITEM_SNOWBALL"), Map.entry("SNOW_SHOVEL", "ITEM_SNOWBALL"),
        Map.entry("SLIME", "ITEM_SLIME"), Map.entry("ITEM_CRACK", "ITEM"),
        Map.entry("BLOCK_CRACK", "BLOCK"), Map.entry("BLOCK_DUST", "BLOCK"),
        Map.entry("WATER_DROP", "RAIN"), Map.entry("MOB_APPEARANCE", "ELDER_GUARDIAN"),
        Map.entry("TOTEM", "TOTEM_OF_UNDYING")
    );

    static Particle resolveParticle(String configured) {
        if (configured == null) return null;
        String name = configured.trim().toUpperCase(Locale.ROOT);
        if (name.startsWith("MINECRAFT:")) name = name.substring("MINECRAFT:".length());
        String canonical = PARTICLE_NAMES.getOrDefault(name, name);
        for (Particle effect : Particle.values())
            if (effect.name().equals(name)) return effect;
        for (Particle effect : Particle.values())
            if (PARTICLE_NAMES.getOrDefault(effect.name(), effect.name()).equals(canonical)) return effect;
        return null;
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
