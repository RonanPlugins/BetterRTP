package me.SuperRonanCraft.BetterRTP.player.rtp.effects;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Particles displayed with the native Bukkit Particle API.
//The old ParticleLib (xyz.xenondevs.particle) only supported MC 1.8-1.19.3 and
//broke on MC 1.21.5+ (particle packet rework), so particle effects are now
//rendered through org.bukkit.Particle on the entity's region thread (Folia-safe).

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
        String typeTrying = null;
        try {
            for (String type : types) {
                typeTrying = type;
                Particle particle = toBukkitParticle(type);
                if (particle != null)
                    effects.add(particle);
            }
        } catch (NullPointerException e) {
            effects.clear();
            effects.add(Particle.POOF);
            getPl().getLogger().severe("The particle '" + typeTrying + "' doesn't exist! Default particle enabled... " +
                    "Try using '/rtp info particles' to get a list of available particles");
        }
        if (effects.isEmpty())
            effects.add(Particle.POOF);
        shape = config.getString("Particles.Shape").toUpperCase();
        if (!Arrays.asList(shapeTypes).contains(shape)) {
            getPl().getLogger().severe("The particle shape '" + shape + "' doesn't exist! Default particle shape enabled...");
            getPl().getLogger().severe("Try using '/rtp info shapes' to get a list of shapes, or: " + Arrays.asList(shapeTypes));
            shape = shapeTypes[0];
        }
    }

    //Map the old ParticleLib enum names to modern Bukkit particle names (MC 1.13+ flattening)
    private Particle toBukkitParticle(String type) {
        String name = type.toUpperCase();
        switch (name) {
            case "EXPLOSION_NORMAL": name = "POOF"; break;
            case "EXPLOSION_LARGE": name = "EXPLOSION"; break;
            case "EXPLOSION_HUGE": name = "EXPLOSION_EMITTER"; break;
            case "CRIT_MAGIC": name = "ENCHANTED_HIT"; break;
            case "SMOKE_NORMAL": name = "SMOKE"; break;
            case "SMOKE_LARGE": name = "LARGE_SMOKE"; break;
            case "SPELL": name = "EFFECT"; break;
            case "SPELL_INSTANT": name = "INSTANT_EFFECT"; break;
            case "SPELL_MOB": name = "ENTITY_EFFECT"; break;
            case "SPELL_MOB_AMBIENT": name = "AMBIENT_ENTITY_EFFECT"; break;
            case "SPELL_WITCH": name = "WITCH"; break;
            case "DRIP_WATER": name = "DRIPPING_WATER"; break;
            case "DRIP_LAVA": name = "DRIPPING_LAVA"; break;
            case "VILLAGER_ANGRY": name = "ANGRY_VILLAGER"; break;
            case "VILLAGER_HAPPY": name = "HAPPY_VILLAGER"; break;
            case "ENCHANTMENT_TABLE": name = "ENCHANT"; break;
            case "REDSTONE": name = "DUST"; break;
            case "SNOWBALL": name = "ITEM_SNOWBALL"; break;
            case "SNOW_SHOVEL": name = "SNOWFLAKE"; break;
            case "SLIME": name = "ITEM_SLIME"; break;
            case "MOB_APPEARANCE": name = "ELDER_GUARDIAN"; break;
            case "ITEM_CRACK": name = "ITEM"; break;
            case "BLOCK_CRACK": name = "BLOCK"; break;
            case "BLOCK_DUST": name = "BLOCK"; break;
            case "WATER_DROP": name = "RAIN"; break;
            case "TOWN_AURA": name = "MYCELIUM_UMBRELLA"; break;
            //same names in both: CRIT, NOTE, PORTAL, FLAME, LAVA, CLOUD, HEART, BARRIER,
            //ASH, DRAGON_BREATH, END_ROD, DAMAGE_INDICATOR, SWEEP_ATTACK, FALLING_DUST,
            //TOTEM, SPIT, SQUID_INK, BUBBLE_POP, CURRENT_DOWN, BUBBLE_COLUMN_UP, NAUTILUS, DOLPHIN
            default: break;
        }
        try {
            return Particle.valueOf(name);
        } catch (IllegalArgumentException e) {
            getPl().getLogger().severe("The particle '" + type + "' doesn't exist in this server version!");
            return null;
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
                spawn(loc.clone().add(vec), new Vector(0, -0.125, 0), .15f, effect, p);
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
                    spawn(loc.clone().add(vec), new Vector(0, 0, 0), 0f, effect, p);
                }
            }
    }

    private void partExplosion(Player p) { //Particles with a shape and forward velocity
        Location loc = p.getLocation().add(new Vector(0, 1, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index);
            for (Particle effect : effects) {
                spawn(loc.clone().add(vec), vec, 1.5f, effect, p);
            }
        }
    }

    //count=0 -> offset is used as velocity, extra as speed (same semantics as the old ParticleLib display call)
    private void spawn(Location loc, Vector offset, float speed, Particle effect, Player p) {
        p.spawnParticle(effect, loc.getX(), loc.getY(), loc.getZ(), 0,
                offset.getX(), offset.getY(), offset.getZ(), speed);
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

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
