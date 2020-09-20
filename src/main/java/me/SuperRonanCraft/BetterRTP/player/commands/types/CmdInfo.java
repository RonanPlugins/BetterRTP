package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPParticles;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.worlds.RTP_WORLD_TYPE;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class CmdInfo implements RTPCommand {

    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase(CmdInfoSub.PARTICLES.name()))
                infoParticles(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.SHAPES.name()))
                infoShapes(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.POTION_EFFECTS.name()))
                infoEffects(sendi);
            else
                infoWorlds(sendi);
        } else
            infoWorlds(sendi);
    }

    enum CmdInfoSub { //Sub commands, future expansions
        PARTICLES, SHAPES, POTION_EFFECTS
    }

    private void infoParticles(CommandSender sendi) {
        List<String> info = new ArrayList<>();
        Main pl = Main.getInstance();

        for (ParticleEffect eff : ParticleEffect.VALUES) {
            if (info.isEmpty()) {
                info.add("&7" + eff.name() + "&r");
            } else if (info.size() % 2 == 0) {
                info.add("&7" + eff.name() + "&r");
            } else
                info.add("&f" + eff.name() + "&r");
        }

        info.forEach(str ->
                info.set(info.indexOf(str), pl.getText().color(str)));
        sendi.sendMessage(info.toString());
    }

    private void infoShapes(CommandSender sendi) {
        List<String> info = new ArrayList<>();
        Main pl = Main.getInstance();

        for (String shape : RTPParticles.shapeTypes) {
            if (info.isEmpty()) {
                info.add("&7" + shape + "&r");
            } else if (info.size() % 2 == 0) {
                info.add("&7" + shape + "&r");
            } else
                info.add("&f" + shape + "&r");
        }

        info.forEach(str ->
                info.set(info.indexOf(str), pl.getText().color(str)));
        sendi.sendMessage(info.toString());
    }

    private void infoWorlds(CommandSender sendi) {
        List<String> info = new ArrayList<>();
        info.add("&e&m-----&6 BetterRTP Info &e&m-----");
        Main pl = Main.getInstance();
        for (World w : Bukkit.getWorlds()) {
            info.add("&aWorld: &7" + w.getName());
            if (pl.getRTP().getDisabledWorlds().contains(w.getName())) //DISABLED
                info.add("&7- &6Disabled: &bTrue");
            else {
                info.add("&7- &6Disabled: &cFalse");
                if (pl.getRTP().overriden.containsKey(w.getName()))
                    info.add("&7- &6Overriden: &bTrue");
                else {
                    info.add("&7- &6WorldType: &f" + pl.getRTP().world_type.getOrDefault(w.getName(), RTP_WORLD_TYPE.NORMAL).name());
                    info.add("&7- &6Overriden: &cFalse");
                    RTPWorld _rtpworld = pl.getRTP().defaultWorld;
                    for (RTPWorld __rtpworld : pl.getRTP().customWorlds.values()) {
                        if (__rtpworld.getWorld() != null && __rtpworld.getWorld().getName().equals(w.getName())) {
                            _rtpworld = __rtpworld;
                            break;
                        }
                    }
                    if (_rtpworld == pl.getRTP().defaultWorld)
                        info.add("&7- &6Custom: &cFalse");
                    else
                        info.add("&7- &6Custom: &bTrue");
                    if (_rtpworld.getUseWorldborder()) {
                        info.add("&7- &6UseWorldborder: &bTrue");
                        WorldBorder border = w.getWorldBorder();
                        info.add("&7- &6Center X: &7" + border.getCenter().getBlockX());
                        info.add("&7- &6Center Z: &7" + border.getCenter().getBlockZ());
                        info.add("&7- &6MaxRad: &7" + (border.getSize() / 2));
                    } else {
                        info.add("&7- &6UseWorldborder: &cFalse");
                        info.add("&7- &6Center X: &7" + _rtpworld.getCenterX());
                        info.add("&7- &6Center Z: &7" + _rtpworld.getCenterZ());
                        info.add("&7- &6MaxRad: &7" + _rtpworld.getMaxRad());
                    }
                    info.add("&7- &6MinRad: &7" + _rtpworld.getMinRad());
                }
            }
        }
        info.forEach(str ->
                info.set(info.indexOf(str), pl.getText().color(str)));
        sendi.sendMessage(info.toArray(new String[0]));
    }

    void infoEffects(CommandSender sendi) {
        List<String> info = new ArrayList<>();
        Main pl = Main.getInstance();

        for (PotionEffectType effect : PotionEffectType.values()) {
            if (info.isEmpty()) {
                info.add("&7" + effect.getName() + "&r");
            } else if (info.size() % 2 == 0) {
                info.add("&7" + effect.getName() + "&r");
            } else
                info.add("&f" + effect.getName() + "&r");
        }

        info.forEach(str ->
                info.set(info.indexOf(str), pl.getText().color(str)));
        sendi.sendMessage(info.toString());
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> info = new ArrayList<>();
        if (args.length == 2) {
            for (CmdInfoSub cmd : CmdInfoSub.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase()))
                    info.add(cmd.name().toLowerCase());
        }
        return info;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getInfo(sendi);
    }
}
