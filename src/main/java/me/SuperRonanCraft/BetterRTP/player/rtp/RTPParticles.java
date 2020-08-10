package me.SuperRonanCraft.BetterRTP.player.rtp;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Arrays;
import java.util.Random;

public class RTPParticles {

    private boolean enabled;
    private ParticleEffect effect;
    private String shape;
    private int radius = 30, precision = 180; //Vector weirdness if allowed to be editable
    private double pHeight = 1.75;

    //Some particles act very differently and might not care how they are shaped before animating, ex: EXPLOSION_NORMAL
    public static String[] shapeTypes = {
            "SCAN", //Body scan
            "EXPLOSIVE", //Make an explosive entrance
            "TELEPORT" //Startrek type of portal
            };

    void load() {
        FileBasics.FILETYPE config = getPl().getFiles().getType(FileBasics.FILETYPE.CONFIG);
        enabled = config.getBoolean("Settings.Particles.Enabled");
        if (!enabled) return;
        //Enabled? Load all this junk
        String type = config.getString("Settings.Particles.Type");
        try {
            effect = ParticleEffect.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            effect = ParticleEffect.ASH;
            getPl().getLogger().severe("The particle '" + type + "' doesn't exist! Default particle enabled...");
        }
        shape = config.getString("Settings.Particles.Shape").toUpperCase();
        if (!Arrays.asList(shapeTypes).contains(shape)) {
            getPl().getLogger().severe("The particle shape '" + shape + "' doesn't exist! Default particle shape enabled...");
            getPl().getLogger().severe("Try using one of the following: " + Arrays.asList(shapeTypes).toString());
            shape = shapeTypes[0];
        }
    }

    void display(Player p) {
        if (!enabled) return;
        try { //Incase the library errors out
            switch (shape) {
                case "TELEPORT": partTeleport(p); break;
                case "EXPLOSIVE": partExplosion(p); break;
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
            effect.display(loc.clone().add(vec), new Vector(0, -0.125, 0), 1f, 0, null);
        }
    }

    private void partTeleport(Player p) { //Static particles in a shape
        Random ran = new Random();
        Location loc = p.getLocation().add(new Vector(0, 0, 0));
        for (int index = 1; index < precision; index++) {
            double yran = ran.nextGaussian() * pHeight;
            Vector vec = getVecCircle(index, precision, radius).add(new Vector(0, yran, 0));
            effect.display(loc.clone().add(vec));
        }
    }

    private void partExplosion(Player p) { //Particles with a shape and forward velocity
        Location loc = p.getLocation().add(new Vector(0, 0, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index, precision, radius);
            effect.display(loc.clone().add(vec), vec, 1f, 0, null);
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

    private Main getPl() {
        return Main.getInstance();
    }
}
