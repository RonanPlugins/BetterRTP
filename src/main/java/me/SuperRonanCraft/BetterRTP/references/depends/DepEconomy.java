package me.SuperRonanCraft.BetterRTP.references.depends;

import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class DepEconomy {
    private Economy e;
    private int hunger = 0;
    private boolean checked = false;

    public boolean charge(CommandSender sendi, WorldPlayer pWorld) {
        check(false);
        Player player = pWorld.getPlayer();
        //Hunger Stuff
        boolean took_food = false;
        if (hunger != 0
                && sendi == player
                && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            boolean has_hunger = player.getFoodLevel() > hunger;
            if (!has_hunger) {
                BetterRTP.getInstance().getText().getFailedHunger(sendi);
                return false;
            } else {
                player.setFoodLevel(player.getFoodLevel() - hunger);
                took_food = true;
            }
        }
        //Economy Stuff
        if (e != null && pWorld.getPrice() != 0 && !PermissionNode.BYPASS_ECONOMY.check(sendi)) {
            try {
                EconomyResponse r = e.withdrawPlayer(player, pWorld.getPrice());
                boolean passed_economy = r.transactionSuccess();
                if (!passed_economy) {
                    BetterRTP.getInstance().getText().getFailedPrice(sendi, pWorld.getPrice());
                    if (took_food)
                        player.setFoodLevel(player.getFoodLevel() + hunger);
                } else
                    pWorld.eco_money_taken = true;
                return passed_economy;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Default value
        return true;
    }

    public boolean hasBalance(CommandSender sendi, WorldPlayer pWorld) {
        check(false);
        Player player = pWorld.getPlayer();
        //Hunger Stuff
        if (hunger != 0
                && !PermissionNode.BYPASS_HUNGER.check(sendi)
                && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            boolean has_hunger = player.getFoodLevel() > hunger;
            if (!has_hunger) {
                BetterRTP.getInstance().getText().getFailedHunger(sendi);
                return false;
            }
        }
        //Economy Stuff
        if (e != null && pWorld.getPrice() != 0 && !PermissionNode.BYPASS_ECONOMY.check(sendi)) {
            try {
                boolean passed_economy = e.getBalance(player) >= pWorld.getPrice();
                if (!passed_economy) {
                    BetterRTP.getInstance().getText().getFailedPrice(sendi, pWorld.getPrice());
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Default value
        return true;
    }

    /*public void unCharge(Player p, WorldPlayer pWorld) {
        if (e != null && pWorld.getPrice() != 0 && pWorld.eco_money_taken)
            e.depositPlayer(p, pWorld.getPrice());
    }*/

    public void load() {
        check(true);
    }

    private void check(boolean force) {
        if (!checked || force)
            registerEconomy();
        if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Hunger.Enabled"))
            hunger = BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getInt("Hunger.Honches");
        else
            hunger = 0;
    }

    private void registerEconomy() {
        try {
            if (BetterRTP.getInstance().getFiles().getType(FileBasics.FILETYPE.ECO).getBoolean("Economy.Enabled"))
                if (BetterRTP.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {
                    RegisteredServiceProvider<Economy> rsp = BetterRTP.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
                    e = rsp.getProvider();
                }
        } catch (NullPointerException e) {
            //
        }
        checked = true;
    }
}
