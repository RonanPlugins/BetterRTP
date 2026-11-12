package me.SuperRonanCraft.BetterRTP.references.depends;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DepEssentials {

    public static void setBackLocation(Player player, Location location) {
        try {
            Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
            if (ess == null)
                return;
            User user = ess.getUser(player.getUniqueId());
            if (user == null)
                return;
            user.setLastLocation(location);
        } catch (ClassCastException e) {
            //Something wrong happened, idk why this would break...
        }
    }

}
