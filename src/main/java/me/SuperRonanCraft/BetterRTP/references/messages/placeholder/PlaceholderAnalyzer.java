package me.SuperRonanCraft.BetterRTP.references.messages.placeholder;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        if (p instanceof Player)
            str = papi((Player) p, str);
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
        if (str.contains(Placeholders.WORLD.name))
            str = str.replace(Placeholders.WORLD.name, loc.getWorld().getName());
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
        return str;
    }

    private static String biome(String str, Biome biome) {
        if (str.contains(Placeholders.BIOME.name))
            str = str.replace(Placeholders.BIOME.name, biome.name());
        return str;
    }
}
