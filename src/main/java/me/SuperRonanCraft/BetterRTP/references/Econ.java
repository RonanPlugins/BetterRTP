package me.SuperRonanCraft.BetterRTP.references;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {
    private Economy e;
    private boolean checked = false;

    public boolean charge(Player player, int price) {
        check(false);
        //player.sendMessage("Charging = " + (e != null) + " charge = " + price);
        if (e != null)
            if (price != 0) {
                if (!Main.getInstance().getPerms().getEconomy(player)) {
                    EconomyResponse r = e.withdrawPlayer(player, price);
                    return r.transactionSuccess();
                }
                return true;
            }
        return true;
    }

    public void unCharge(Player p, int price) {
        if (e != null)
            if (price != 0)
                e.depositPlayer(p, price);
    }

    public void load() {
        check(true);
    }

    private void check(boolean force) {
        if (!checked || force)
            registerEconomy();
    }

    private void registerEconomy() {
        try {
            if (Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
                if (Main.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {
                    RegisteredServiceProvider<Economy> rsp = Main.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
                    e = rsp.getProvider();
                }
        } catch (NullPointerException e) {
            //
        }
        checked = true;
    }
}
