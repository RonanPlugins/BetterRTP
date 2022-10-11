package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//---
//Credit to @ByteZ1337 for ParticleLib - https://github.com/ByteZ1337/ParticleLib
//
//Use of particle creation
//---

public class RTPParticles {

    private boolean enabled;
    private final List<ParticleEffect> effects = new ArrayList<>();
    private String shape;
    private final int
            radius = 30,
            precision = 180; //Vector weirdness if allowed to be editable
    private final double pHeight = 1.75;

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
                effects.add(ParticleEffect.valueOf(type.toUpperCase()));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            effects.clear();
            effects.add(ParticleEffect.ASH);
            getPl().getLogger().severe("The particle '" + typeTrying + "' doesn't exist! Default particle enabled... " +
                    "Try using '/rtp info particles' to get a list of available particles");
        } catch (ExceptionInInitializerError e2) {
            effects.clear();
            getPl().getLogger().severe("The particle '" + typeTrying + "' created a fatal error when loading particles! Your MC version isn't supported!");
            enabled = false;
        }
        shape = config.getString("Particles.Shape").toUpperCase();
        if (!Arrays.asList(shapeTypes).contains(shape)) {
            getPl().getLogger().severe("The particle shape '" + shape + "' doesn't exist! Default particle shape enabled...");
            getPl().getLogger().severe("Try using '/rtp info shapes' to get a list of shapes, or: " + Arrays.asList(shapeTypes).toString());
            shape = shapeTypes[0];
        }
    }

    void display(Player p) {
        if (!enabled) return;
        try { //Incase the library errors out
            switch (shape) {
                case "TELEPORT": partTeleport(p); break;
                case "EXPLODE": partExplosion(p); break;
                default: //Super redundant, but... just future proofing
                case "SCAN": partScan(p); break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void partScan(Player p) { //Particles with negative velocity
        Location loc = p.getLocation().add(new Vector(0, pHeight, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index, precision, radius);
            for (ParticleEffect effect : effects) {
                effect.display(loc.clone().add(vec), new Vector(0, -0.125, 0), 1f, 0, null);
            }
        }
    }

    private void partTeleport(Player p) { //Static particles in a shape
        Random ran = new Random();
        Location loc = p.getLocation().add(new Vector(0, 0, 0));
        for (int index = 1; index < precision; index++) {
            double yran = ran.nextGaussian() * pHeight;
            Vector vec = getVecCircle(index, precision, radius).add(new Vector(0, yran, 0));
            for (ParticleEffect effect : effects) {
                effect.display(loc.clone().add(vec));
            }
        }
    }

    private void partExplosion(Player p) { //Particles with a shape and forward velocity
        Location loc = p.getLocation().add(new Vector(0, 1, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index, precision, radius);
            for (ParticleEffect effect : effects) {
                effect.display(loc.clone().add(vec), vec, 1.5f, 0, null);
            }
        }
    }

    private Vector getVecCircle(int index, int precise, int rad) {
        double p1 = (index * Math.PI) / (precise / 2);
        double p2 = (index - 1) * Math.PI / (precise / 2);
        //Positions
        double x1 = Math.cos(p1) * rad;
        double x2 = Math.cos(p2) * rad;
        double z1 = Math.sin(p1) * rad;
        double z2 = Math.sin(p2) * rad;
        return new Vector(x2 - x1, 0, z2 - z1);
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
