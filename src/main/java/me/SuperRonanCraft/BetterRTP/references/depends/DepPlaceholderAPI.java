package me.SuperRonanCraft.BetterRTP.references.depends;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DepPlaceholderAPI extends PlaceholderExpansion {

    @NotNull
    @Override
    public String getIdentifier() {
        return BetterRTP.getInstance().getDescription().getName().toLowerCase();
    }

    @NotNull
    @Override
    public String getAuthor() {
        return BetterRTP.getInstance().getDescription().getAuthors().get(0);
    }

    @NotNull
    @Override
    public String getVersion() {
        return BetterRTP.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String request) {
        PlayerData data = HelperPlayer.getData(player);
        if (request.equalsIgnoreCase("count")) {
            return String.valueOf(data.getRtpCount());
        }
        return null;
    }
}
