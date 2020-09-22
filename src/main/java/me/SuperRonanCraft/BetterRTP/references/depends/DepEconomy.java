package me.SuperRonanCraft.BetterRTP.references.depends;

import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class DepEconomy {
    private Economy e;
    private int hunger = 0;
    private boolean checked = false;

    public boolean charge(Player player, int price) {
        check(false);
        //Hunger Stuff
        boolean took_food = false;
        if (hunger != 0 && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            boolean has_hunger = player.getFoodLevel() > hunger;
            if (!has_hunger) {
                Main.getInstance().getText().getFailedHunger(player);
                return false;
            } else {
                player.setFoodLevel(player.getFoodLevel() - hunger);
                took_food = true;
            }
        }
        //Economy Stuff
        if (e != null && price != 0 && !Main.getInstance().getPerms().getBypassEconomy(player)) {
            try {
                EconomyResponse r = e.withdrawPlayer(player, price);
                boolean passed_economy = r.transactionSuccess();
                if (!passed_economy) {
                    Main.getInstance().getText().getFailedPrice(player, price);
                    if (took_food)
                        player.setFoodLevel(player.getFoodLevel() + hunger);
                }
                return passed_economy;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Default value
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
        if (Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Hunger.Enabled"))
            hunger = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getInt("Hunger.Honches");
        else
            hunger = 0;
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
