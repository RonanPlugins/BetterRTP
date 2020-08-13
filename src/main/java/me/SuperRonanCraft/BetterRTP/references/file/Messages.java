package me.SuperRonanCraft.BetterRTP.references.file;

import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

public class Messages {
    private Main pl;
    private String preM = "Messages.", preT = "Titles.", preH = "Help.", preU = "Usage.";

    public Messages(Main pl) {
        this.pl = pl;
    }

    private LangFile getLang() {
        return pl.getFiles().getLang();
    }

    public void sms(CommandSender sendi, String msg) {
        if (!msg.equals(""))
            sendi.sendMessage(colorPre(msg));
    }

    public void getSuccessPaid(CommandSender sendi, int price, String x, String y, String z, String world, int
            attempts) {
        sms(sendi, getLang().getString(preM + "Success.Paid").replaceAll("%price%", String.valueOf(price)).replaceAll
                ("%x%", x).replaceAll("%y%", y).replaceAll("%z%", z).replaceAll("%world%", world).replaceAll
                ("%attempts%", Integer.toString(attempts)));
    }

    public void getSuccessBypass(CommandSender sendi, String x, String y, String z, String world, int attemtps) {
        sms(sendi, getLang().getString(preM + "Success.Bypass").replaceAll("%x%", x).replaceAll("%y%", y).replaceAll
                ("%z%", z).replaceAll("%world%", world).replaceAll("%attempts%", Integer.toString(attemtps)));
    }

    public void getSuccessLoading(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Success.Loading"));
    }

    public void getFailedNotSafe(CommandSender sendi, int attempts) {
        sms(sendi, getLang().getString(preM + "Failed.NotSafe").replaceAll("%attempts%", Integer.toString(attempts)));
    }

    public void getFailedPrice(CommandSender sendi, int price) {
        sms(sendi, getLang().getString(preM + "Failed.Price").replaceAll("%price%", String.valueOf(price)));
    }

    public void getOtherNotSafe(CommandSender sendi, int attempts, String player) {
        sms(sendi, getLang().getString(preM + "Other.NotSafe").replaceAll("%attempts%", Integer.toString(attempts))
                .replaceAll("%player%", player));
    }

    public void getOtherSuccess(CommandSender sendi, String player, String x, String y, String z, String world, int
            attempts) {
        sms(sendi, getLang().getString(preM + "Other.Success").replaceAll("%player%", player).replaceAll("%x%", x)
                .replaceAll("%y%", y).replaceAll("%z%", z).replaceAll("%world%", world).replaceAll("%attempts%",
                        Integer.toString(attempts)));
    }

    public void getOtherBiome(CommandSender sendi, String biome) {
        sms(sendi, getLang().getString(preM + "Other.Biome").replaceAll("%biome%", biome));
    }

    public void getNotExist(CommandSender sendi, String world) {
        sms(sendi, getLang().getString(preM + "NotExist").replaceAll("%world%", world));
    }

    public void getReload(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Reload"));
    }

    public void getNoPermission(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "NoPermission.Basic"));
    }

    public void getNoPermissionWorld(CommandSender sendi, String world) {
        sms(sendi, getLang().getString(preM + "NoPermission.World").replaceAll("%world%", world));
    }

    public void getDisabledWorld(CommandSender sendi, String world) {
        sms(sendi, getLang().getString(preM + "DisabledWorld").replaceAll("%world%", world));
    }

    public void getCooldown(CommandSender sendi, String time) {
        sms(sendi, getLang().getString(preM + "Cooldown").replaceAll("%time%", time));
    }

    public void getInvalid(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preM + "Invalid").replaceAll("%command%", cmd));
    }

    public void getNotOnline(CommandSender sendi, String player) {
        sms(sendi, getLang().getString(preM + "NotOnline").replaceAll("%player%", player));
    }

    public void getDelay(CommandSender sendi, int time) {
        sms(sendi, getLang().getString(preM + "Delay").replaceAll("%time%", String.valueOf(time)));
    }

    public void getSignCreated(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preM + "Sign").replaceAll("%command%", cmd));
    }

    public void getMoved(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Moved"));
    }

    public void getAlready(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Already"));
    }

    private String getPrefix() {
        return getLang().getString(preM + "Prefix");
    }

    public String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public String colorPre(String str) {
        return color(getPrefix() + str);
    }

    //Help
    public void getHelpList(CommandSender sendi, String cmd) {
        for (String s : getLang().getStringList(preH + "List"))
            sms(sendi, s.replaceAll("%command%", cmd));
    }

    public void getHelpPlayer(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preH + "Player").replaceAll("%command%", cmd));
    }

    public void getHelpWorld(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preH + "World").replaceAll("%command%", cmd));
    }

    public void getHelpReload(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preH + "Reload").replaceAll("%command%", cmd));
    }

    //Usage
    public void getUsageRTPOther(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Player").replaceAll("%command%", cmd));
    }

    public void getUsageWorld(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "World").replaceAll("%command%", cmd));
    }

    public void getUsageBiome(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Biome").replaceAll("%command%", cmd));
    }

    public void getUsageEdit(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Edit").replaceAll("%command%", cmd));
    }

    // Not Found
    public void error(CommandSender sendi) {
        sms(sendi, "&cERROR &7Seems like your Administrator did not update their language file!");
    }
}
