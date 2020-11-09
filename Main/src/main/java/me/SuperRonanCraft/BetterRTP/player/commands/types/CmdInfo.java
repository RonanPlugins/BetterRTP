package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPParticles;
import me.SuperRonanCraft.BetterRTP.references.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.worlds.WORLD_TYPE;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class CmdInfo implements RTPCommand, RTPCommandHelpable {

    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase(CmdInfoSub.PARTICLES.name()))
                infoParticles(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.SHAPES.name()))
                infoShapes(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.POTION_EFFECTS.name()))
                infoEffects(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.WORLD.name())) {
                if (sendi instanceof Player) { //Personalize with permission groups
                    World world = null;
                    boolean personal = false;
                    if (args.length > 2) {
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player != null) {
                            world = player.getWorld();
                            personal = true;
                        }
                    }
                    if (world == null)
                        world = ((Player) sendi).getWorld();
                    sendInfoWorld(sendi, infoGetWorld(sendi, world, personal));
                } else
                    infoWorld(sendi);
            }
        } else
            infoWorld(sendi);
    }

    @Override
    public String getHelp() {
        return Main.getInstance().getText().getHelpInfo();
    }

    enum CmdInfoSub { //Sub commands, future expansions
        PARTICLES, SHAPES, POTION_EFFECTS, WORLD
    }

    //Particles
    private void infoParticles(CommandSender sendi) {
        List<String> info = new ArrayList<>();
        Main pl = Main.getInstance();

        for (ParticleEffect eff : ParticleEffect.VALUES) {
            if (info.isEmpty() || info.size() % 2 == 0) {
                info.add("&7" + eff.name() + "&r");
            } else
                info.add("&f" + eff.name() + "&r");
        }

        info.forEach(str ->
                info.set(info.indexOf(str), pl.getText().color(str)));
        sendi.sendMessage(info.toString());
    }

    //Shapes
    private void infoShapes(CommandSender sendi) {
        List<String> info = new ArrayList<>();

        for (String shape : RTPParticles.shapeTypes) {
            if (info.isEmpty() || info.size() % 2 == 0) {
                info.add("&7" + shape + "&r");
            } else
                info.add("&f" + shape + "&r");
        }

        info.forEach(str ->
                info.set(info.indexOf(str), Main.getInstance().getText().color(str)));
        sendi.sendMessage(info.toString());
    }

    //World
    private void sendInfoWorld(CommandSender sendi, List<String> list) { //Send info
        list.add(0, "&e&m-----&6 BetterRTP &8| Info &e&m-----");
        list.forEach(str ->
                list.set(list.indexOf(str), Main.getInstance().getText().color(str)));
        sendi.sendMessage(list.toArray(new String[0]));
    }

    private void infoWorld(CommandSender sendi) { //All worlds
        List<String> info = new ArrayList<>();
        for (World w : Bukkit.getWorlds())
            info.addAll(infoGetWorld(sendi, w, false));
        sendInfoWorld(sendi, info);
    }

    private List<String> infoGetWorld(CommandSender sendi, World w, boolean personal) { //Specific world
        List<String> info = new ArrayList<>();
        Main pl = Main.getInstance();
        String _true = "&aTrue", _false = "&bFalse";
        info.add("&eWorld: &7" + w.getName() + (personal ? " &7(personalized)" : ""));
        if (personal)
            info.add("&7- &6Allowed: " + (pl.getPerms().getAWorld(sendi, w.getName()) ? _true : _false));
        if (pl.getRTP().getDisabledWorlds().contains(w.getName())) //DISABLED
            info.add("&7- &6Disabled: " + _true);
        else {
            info.add("&7- &6Disabled: " + _false);
            if (pl.getRTP().overriden.containsKey(w.getName()))
                info.add("&7- &6Overriden: " + _true + " &7(" + pl.getRTP().overriden.get(w.getName()) + ")");
            else {
                info.add("&7- &6WorldType: &f" + pl.getRTP().world_type.getOrDefault(w.getName(), WORLD_TYPE.NORMAL).name());
                info.add("&7- &6Overriden: " + _false);
                WorldPlayer _rtpworld = Main.getInstance().getRTP().getPlayerWorld(sendi, w.getName(), null, personal);
                info.add("&7- &6Custom: " + (Main.getInstance().getRTP().customWorlds.containsKey(w.getName()) ? _true : _false));
                info.add("&7- &6UseWorldBorder: " + (_rtpworld.getUseWorldborder() ? _true : _false));
                info.add("&7- &6Permission Group: " + (_rtpworld.getConfig() != null ? "&e" + _rtpworld.getConfig().name : "&cN/A"));
                info.add("&7- &6Center X: &f" + _rtpworld.getCenterX());
                info.add("&7- &6Center Z: &f" + _rtpworld.getCenterZ());
                info.add("&7- &6MaxRad: &f" + _rtpworld.getMaxRad());
                info.add("&7- &6MinRad: &f" + _rtpworld.getMinRad());
            }
        }
        return info;
    }

    //Effects
    private void infoEffects(CommandSender sendi) {
        List<String> info = new ArrayList<>();

        for (PotionEffectType effect : PotionEffectType.values()) {
            if (info.isEmpty() || info.size() % 2 == 0) {
                info.add("&7" + effect.getName() + "&r");
            } else
                info.add("&f" + effect.getName() + "&r");
        }

        info.forEach(str ->
                info.set(info.indexOf(str), Main.getInstance().getText().color(str)));
        sendi.sendMessage(info.toString());
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> info = new ArrayList<>();
        if (args.length == 2) {
            for (CmdInfoSub cmd : CmdInfoSub.values())
                if (cmd.name().toLowerCase().startsWith(args[1].toLowerCase()))
                    info.add(cmd.name().toLowerCase());
        } else if (args.length == 3) {
            if (CmdInfoSub.WORLD.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                        info.add(p.getName());
                }
            }
        }
        return info;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getInfo(sendi);
    }
}
