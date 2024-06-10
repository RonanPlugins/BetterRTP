package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import net.william278.husktowns.api.BukkitHuskTownsAPI;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class RTP_HuskTowns implements RegionPluginCheck {

    private final BukkitHuskTownsAPI huskTownsAPI;

    // NOT TESTED (3.6.13)
    // HuskTowns (v3.0.4)
    // https://www.spigotmc.org/resources/husktowns.92672/
    public RTP_HuskTowns(JavaPlugin plugin) {
        this.huskTownsAPI = BukkitHuskTownsAPI.getInstance();
    }

    @Override
    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.HUSKTOWNS.isEnabled()) {
            try {
                result = !huskTownsAPI.getClaimAt(loc).isPresent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
