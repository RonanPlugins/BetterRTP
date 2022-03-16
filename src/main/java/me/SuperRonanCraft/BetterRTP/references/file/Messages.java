package me.SuperRonanCraft.BetterRTP.references.file;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages {
    private final String preM = "Messages.", preH = "Help.", preU = "Usage.";

    private LangFile getLang() {
        return BetterRTP.getInstance().getFiles().getLang();
    }

    public void sms(CommandSender sendi, String msg) {
        if (!msg.equals(""))
            sendi.sendMessage(colorPre(msg));
    }

    public void sms(CommandSender sendi, List<String> msg) {
        if (msg != null && !msg.isEmpty()) {
            msg.forEach(str ->
                    msg.set(msg.indexOf(str), color(str)));
            sendi.sendMessage(msg.toArray(new String[0]));
        }
    }

    //SUCCESS
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

    public void getSuccessTeleport(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Success.Teleport"));
    }

    //FAILED
    public void getFailedNotSafe(CommandSender sendi, int attempts) {
        sms(sendi, getLang().getString(preM + "Failed.NotSafe").replaceAll("%attempts%", Integer.toString(attempts)));
    }

    public void getFailedPrice(CommandSender sendi, int price) {
        sms(sendi, getLang().getString(preM + "Failed.Price").replaceAll("%price%", String.valueOf(price)));
    }

    public void getFailedHunger(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Failed.Hunger"));
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
        return translateHexColorCodes(str);
    }

    //Thank you to zwrumpy on Spigot! (https://www.spigotmc.org/threads/hex-color-code-translate.449748/#post-4270781)
    //Supports 1.8 to 1.18
    private static String translateHexColorCodes(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String colorPre(String str) {
        return color(getPrefix() + str);
    }

    //Edit
    public void getEditError(CommandSender sendi) {
        sms(sendi, getLang().getString(preM + "Edit.Error"));
    }

    public void getEditSet(CommandSender sendi, String type, String value) {
        sms(sendi, getLang().getString(preM + "Edit.Set").replaceAll("%type%", type).replaceAll("%value%", value));
    }

    public void getEditRemove(CommandSender sendi, String world) {
        sms(sendi, getLang().getString(preM + "Edit.Remove").replaceAll("%world%", world));
    }

    //Help
    public String getHelpPrefix() {
        return getLang().getString(preH + "Prefix");
    }

    public String getHelpMain() { //rtp
        return getLang().getString(preH + "Main");
    }

    public String getHelpBiome() { //rtp biome
        return getLang().getString(preH + "Biome");
    }

    public String getHelpEdit() { //rtp edit
        return getLang().getString(preH + "Edit");
    }

    public String getHelpHelp() { //rtp help
        return getLang().getString(preH + "Help");
    }

    public String getHelpInfo() { //rtp info
        return getLang().getString(preH + "Info");
    }

    public String getHelpPlayer() { //rtp player
        return getLang().getString(preH + "Player");
    }

    public String getHelpReload() {
        return getLang().getString(preH + "Reload");
    }

    public String getHelpSettings() { //rtp settings
        return getLang().getString(preH + "Settings");
    }

    public String getHelpTest() { //rtp test
        return getLang().getString(preH + "Test");
    }

    public String getHelpVersion() { //rtp version
        return getLang().getString(preH + "Version");
    }

    public String getHelpWorld() { //rtp world
        return getLang().getString(preH + "World");
    }

    public String getHelpLocation() { //rtp location
        return getLang().getString(preH + "Location");
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

    public void getUsageLocation(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Location").replaceAll("%command%", cmd));
    }

    public void getUsageEdit(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Edit.Base").replaceAll("%command%", cmd));
    }

    public void getUsageEditDefault(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Edit.Default").replaceAll("%command%", cmd));
    }

    public void getUsageEditWorld(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Edit.World").replaceAll("%command%", cmd));
    }

    public void getUsageWorldtype(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Edit.Worldtype").replaceAll("%command%", cmd));
    }

    public void getUsageOverride(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Edit.Override").replaceAll("%command%", cmd));
    }

    public void getUsageBlacklistedBlocks(CommandSender sendi, String cmd) {
        sms(sendi, getLang().getString(preU + "Edit.BlacklistedBlocks").replaceAll("%command%", cmd));
    }

    // Not Found
    public void error(CommandSender sendi) {
        sms(sendi, "&cERROR &7Seems like your Administrator did not update their language file!");
    }

    public void getNotPlayer(CommandSender sendi, String cmd) {
        sms(sendi, "Must be a player to use this command! Try '/" + cmd + " help'");
    }
}
