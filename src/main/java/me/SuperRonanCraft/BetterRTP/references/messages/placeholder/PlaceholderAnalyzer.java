package me.SuperRonanCraft.BetterRTP.references.messages.placeholder;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderAnalyzer {

    public static String applyPlaceholders(CommandSender p, String str, Object info) {
        if (info instanceof String)
            str = string(str, (String) info);
        if (info instanceof Player)
            str = player(str, (Player) info);
        if (info instanceof Location)
            str = location(str, (Location) info);
        if (info instanceof Integer)
            str = ints(str, (Integer) info);
        if (info instanceof Biome)
            str = biome(str, (Biome) info);
        if (info instanceof WorldPlayer)
            str = worldPlayer((WorldPlayer) info, str);
        if (info instanceof World)
            str = world(str, (World) info);
        if (info instanceof RTPCommand)
            str = cmd(str, (RTPCommand) info);
        if (info instanceof PermissionNode)
            str = permNode(str, (PermissionNode) info);
        if (info instanceof RTPWorld)
            str = ints(str, ((RTPWorld) info).getPrice());
        if (p instanceof Player)
            str = papi((Player) p, str);
        return str;
    }

    private static String worldPlayer(WorldPlayer pWorld, String str) {
        str = ints(str, pWorld.getPrice());
        str = world(str, pWorld.getWorld());
        str = perm(str, pWorld.getPlayer(), pWorld.getWorld().getName());
        return player(str, pWorld.getPlayer());
    }

    private static String perm(String str, CommandSender player, String world) {
        if (str.contains(Placeholders.PERMISSION.name))
            str = str.replace(Placeholders.PERMISSION.name, PermissionCheck.getAWorldText(player, world).getString());
        return str;
    }

    private static String string(String str, String info) {
        if (str.contains(Placeholders.COMMAND.name))
            str = str.replace(Placeholders.COMMAND.name, info);
        if (str.contains(Placeholders.PLAYER_NAME.name))
            str = str.replaceAll(Placeholders.PLAYER_NAME.name, info);
        if (str.contains(Placeholders.WORLD.name))
            str = str.replaceAll(Placeholders.WORLD.name, info);
        if (str.contains(Placeholders.COOLDOWN.name))
            str = str.replaceAll(Placeholders.COOLDOWN.name, info);
        return str;
    }

    private static String location(String str, Location loc) {
        if (str.contains(Placeholders.LOCATION_X.name))
            str = str.replace(Placeholders.LOCATION_X.name, String.valueOf(loc.getBlockX()));
        if (str.contains(Placeholders.LOCATION_Y.name))
            str = str.replace(Placeholders.LOCATION_Y.name, String.valueOf(loc.getBlockY()));
        if (str.contains(Placeholders.LOCATION_Z.name))
            str = str.replace(Placeholders.LOCATION_Z.name, String.valueOf(loc.getBlockZ()));
        return world(str, loc.getWorld());
    }

    private static String world(String str, World world) {
        if (str.contains(Placeholders.WORLD.name))
            str = str.replace(Placeholders.WORLD.name, world.getName());
        return str;
    }

    private static String player(String str, Player player) {
        if (str.contains(Placeholders.PLAYER_NAME.name))
            str = str.replace(Placeholders.PLAYER_NAME.name, player.getName());
        return str;
    }

    private static String papi(Player player, String str) {
        //Papi
        if (BetterRTP.getInstance().isPlaceholderAPI())
            try {
                str = PlaceholderAPI.setPlaceholders(player, str);
            } catch (Exception e) {
                //Something went wrong with PAPI
            }
        return str;
    }

    private static String ints(String str, int num) {
        if (str.contains(Placeholders.ATTEMPTS.name))
            str = str.replace(Placeholders.ATTEMPTS.name, String.valueOf(num));
        if (str.contains(Placeholders.PRICE.name))
            str = str.replace(Placeholders.PRICE.name, String.valueOf(num));
        if (str.contains(Placeholders.DELAY.name))
            str = str.replace(Placeholders.DELAY.name, String.valueOf(num));
        if (str.contains(Placeholders.TIME.name))
            str = str.replace(Placeholders.TIME.name, String.valueOf(num));
        return str;
    }

    private static String biome(String str, Biome biome) {
        if (str.contains(Placeholders.BIOME.name))
            str = str.replace(Placeholders.BIOME.name, biome.name());
        return str;
    }

    private static String cmd(String str, RTPCommand cmd) {
        if (str.contains(Placeholders.PERMISSION.name))
            str = permNode(str, cmd.permission());
        return str;
    }

    private static String permNode(String str, PermissionCheck perm) {
        if (str.contains(Placeholders.PERMISSION.name))
            str = str.replace(Placeholders.PERMISSION.name, perm.getNode());
        return str;
    }
}
