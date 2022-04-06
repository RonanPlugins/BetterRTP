package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelperRTP_Info {

    //Custom biomes
    public static List<String> getBiomes(String[] args, int start, CommandSender sendi) {
        List<String> biomes = new ArrayList<>();
        boolean error_sent = false;
        if (PermissionNode.BIOME.check(sendi))
            for (int i = start; i < args.length; i++) {
                String str = args[i];
                try {
                    biomes.add(Biome.valueOf(str.replaceAll(",", "").toUpperCase()).name());
                } catch (Exception e) {
                    if (!error_sent) {
                        BetterRTP.getInstance().getText().getOtherBiome(sendi, str);
                        error_sent = true;
                    }
                }
            }
        return biomes;
    }

    public static void addBiomes(List<String> list, String[] args) {
        try {
            for (Biome b : Biome.values())
                if (b.name().toUpperCase().replaceAll("minecraft:", "").startsWith(args[args.length - 1].toUpperCase()))
                    list.add(b.name().replaceAll("minecraft:", ""));
        } catch (NoSuchMethodError e) {
            //Not in 1.14.X
        }
    }

}
