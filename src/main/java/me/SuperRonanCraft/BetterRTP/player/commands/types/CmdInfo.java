package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommandHelpable;
import me.SuperRonanCraft.BetterRTP.player.commands.RTP_SETUP_TYPE;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPParticles;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.QueueHandler;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldDefault;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class CmdInfo implements RTPCommand, RTPCommandHelpable {

    public String getName() {
        return "info";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase(CmdInfoSub.PARTICLES.name()))
                infoParticles(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.SHAPES.name()))
                infoShapes(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.POTION_EFFECTS.name()))
                infoEffects(sendi);
            else if (args[1].equalsIgnoreCase(CmdInfoSub.WORLD.name())) {
                if (args.length > 2 && Bukkit.getWorld(args[2]) != null) {
                    sendInfoWorld(sendi, infoGetWorld(sendi, Bukkit.getWorld(args[2]), null, null));
                } else if (sendi instanceof Player) { //Personalize with permission groups
                    World world = null;
                    Player player = null;
                    if (args.length > 2) {
                        player = Bukkit.getPlayer(args[2]);
                        if (player != null)
                            world = player.getWorld();
                    }
                    if (world == null)
                        world = ((Player) sendi).getWorld();
                    sendInfoWorld(sendi, infoGetWorld(sendi, world, player, null));
                } else
                    infoWorld(sendi);
            }
        } else
            infoWorld(sendi);
    }

    @Override
    public String getHelp() {
        return BetterRTP.getInstance().getText().getHelpInfo();
    }

    enum CmdInfoSub { //Sub commands, future expansions
        PARTICLES, SHAPES, POTION_EFFECTS, WORLD
    }

    //Particles
    private void infoParticles(CommandSender sendi) {
        List<String> info = new ArrayList<>();
        BetterRTP pl = BetterRTP.getInstance();

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
                info.set(info.indexOf(str), BetterRTP.getInstance().getText().color(str)));
        sendi.sendMessage(info.toString());
    }

    //World
    public static void sendInfoWorld(CommandSender sendi, List<String> list) { //Send info
        list.add(0, "&e&m-----&6 BetterRTP &8| Info &e&m-----");
        list.forEach(str ->
                list.set(list.indexOf(str), BetterRTP.getInstance().getText().color(str)));
        sendi.sendMessage(list.toArray(new String[0]));
    }

    private void infoWorld(CommandSender sendi) { //All worlds
        List<String> info = new ArrayList<>();
        for (World w : Bukkit.getWorlds())
            info.addAll(infoGetWorld(sendi, w, null, null));
        sendInfoWorld(sendi, info);
    }

    public static List<String> infoGetWorld(CommandSender sendi, World w, Player player, WorldPlayer _rtpworld) { //Specific world
        List<String> info = new ArrayList<>();
        BetterRTP pl = BetterRTP.getInstance();
        String _true = "&aTrue", _false = "&bFalse";
        info.add("&bRTP info for &7" + w.getName() + (player != null ? " &d(personalized)" : ""));
        info.add("&7- &eViewing as: &b" + (player != null ? player.getName() : "ADMIN"));
        info.add("&7- &6Allowed: " + (player != null ? PermissionNode.getAWorld(player, w.getName()) ? _true : _false : "&cN/A"));
        if (pl.getRTP().getDisabledWorlds().contains(w.getName())) //World disabled
            info.add("&7- &eDisabled: " + _true);
        else {
            info.add("&7- &eDisabled: " + _false);
            if (pl.getRTP().overriden.containsKey(w.getName())) //World Overriden
                info.add("&7- &6Overriden: " + _true + " &7- target `" + pl.getRTP().overriden.get(w.getName()) + "`");
            else {
                info.add("&7- &6Overriden&7: " + _false);
                if (_rtpworld == null)
                    _rtpworld = BetterRTP.getInstance().getRTP().getPlayerWorld(new RTPSetupInformation(w, player != null ? player : sendi, player, player != null));
                WorldDefault worldDefault = BetterRTP.getInstance().getRTP().RTPdefaultWorld;
                info.add("&7- &eSetup Type&7: " + _rtpworld.setup_type.name() + getInfo(_rtpworld, worldDefault, "setup"));
                info.add("&7- &6Use World Border&7: " + (_rtpworld.getUseWorldborder() ? _true : _false));
                info.add("&7- &eWorld Type&7: &f" + _rtpworld.getWorldtype().name());
                info.add("&7- &6Center X&7: &f" + _rtpworld.getCenterX() + getInfo(_rtpworld, worldDefault, "centerx"));
                info.add("&7- &eCenter Z&7: &f" + _rtpworld.getCenterZ() + getInfo(_rtpworld, worldDefault, "centerz"));
                info.add("&7- &6Max Radius&7: &f" + _rtpworld.getMaxRadius() + getInfo(_rtpworld, worldDefault, "maxrad"));
                info.add("&7- &eMin Radius&7: &f" + _rtpworld.getMinRadius() + getInfo(_rtpworld, worldDefault, "minrad"));
                info.add("&7- &6Min Y&7: &f" + _rtpworld.getMinY());
                info.add("&7- &eMax Y&7: &f" + _rtpworld.getMaxY());
                info.add("&7- &6Price&7: &f" + _rtpworld.getPrice() + getInfo(_rtpworld, worldDefault, "price"));
                info.add("&7- &eCooldown&7: &f" + _rtpworld.getCooldown() + getInfo(_rtpworld, worldDefault, "cooldown"));
                info.add("&7- &6Biomes&7: &f" + _rtpworld.getBiomes().toString());
                info.add("&7- &eShape&7: &f" + _rtpworld.getShape().toString() + getInfo(_rtpworld, worldDefault, "shape"));
                info.add("&7- &6Permission Group&7: " + (_rtpworld.getConfig() != null ? "&a" + _rtpworld.getConfig().getGroupName() : "&cN/A"));
                info.add("&7- &eQueue Available&7: " + QueueHandler.getApplicable(_rtpworld).size());
            }
        }
        return info;
    }

    //Janky, but it works
    private static String getInfo(WorldPlayer worldPlayer, WorldDefault worldDefault, String type) {
        switch (type) {
            case "centerx":
                return worldPlayer.getUseWorldborder() || worldPlayer.getCenterX() == worldDefault.getCenterX() ? worldPlayer.getUseWorldborder() ? " &8(worldborder)" : " &8(default)" : "";
            case "centerz":
                return worldPlayer.getUseWorldborder() || worldPlayer.getCenterZ() == worldDefault.getCenterZ() ? worldPlayer.getUseWorldborder() ? " &8(worldborder)" : " &8(default)" : "";
            case "maxrad":
                return worldPlayer.getUseWorldborder() || worldPlayer.getMaxRadius() == worldDefault.getMaxRadius() ?
                        worldPlayer.getUseWorldborder() ?
                        worldPlayer.getMaxRadius() >= worldPlayer.getWorld().getWorldBorder().getSize() ?
                                " &8(worldborder)" : " &8(custom)" : " &8(default)" : "";
            case "minrad":
                return worldPlayer.getMinRadius() == worldDefault.getMinRadius() ? " &8(default)" : "";
            case "price":
                return worldPlayer.getPrice() == worldDefault.getPrice() ? " &8(default)" : "";
            case "shape":
                return worldPlayer.getShape() == worldDefault.getShape() ? " &8(default)" : "";
            case "setup":
                return worldPlayer.setup_type == RTP_SETUP_TYPE.LOCATION ? " &7(" + worldPlayer.setup_name + ")" : "";
            case "cooldown":
                return worldPlayer.getPlayer() != null ? PermissionNode.BYPASS_COOLDOWN.check(worldPlayer.getPlayer()) ? " &8(bypassing)" : "" : " &cN/A";
        }
        return "";
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
                info.set(info.indexOf(str), BetterRTP.getInstance().getText().color(str)));
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
                for (World world : Bukkit.getWorlds())
                    if (world.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                        info.add(world.getName());
            }
        }
        return info;
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNode.INFO.check(sendi);
    }
}
