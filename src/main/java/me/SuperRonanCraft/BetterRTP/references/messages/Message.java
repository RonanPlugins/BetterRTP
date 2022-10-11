package me.SuperRonanCraft.BetterRTP.references.messages;

import com.google.common.collect.ImmutableCollection;
import lombok.NonNull;
import me.SuperRonanCraft.BetterRTP.references.file.FileData;
import me.ronancraft.AmethystGear.AmethystGear;
import me.ronancraft.AmethystGear.resources.files.FileData;
import me.ronancraft.AmethystGear.resources.messages.placeholderdata.PlaceholderAnalyzer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Message {

    FileData lang();

    static void sms(Message messenger, CommandSender sendi, String msg) {
        if (!msg.isEmpty())
            Bukkit.getScheduler().runTask(AmethystGear.getInstance(), () ->
                    sendi.sendMessage(placeholder(sendi, getPrefix(messenger) + msg)));
    }

    static void sms(Message messenger, CommandSender sendi, String msg, Object placeholderInfo) {
        if (!msg.isEmpty())
            Bukkit.getScheduler().runTask(AmethystGear.getInstance(), () ->
                    sendi.sendMessage(Objects.requireNonNull(placeholder(sendi, getPrefix(messenger) + msg, placeholderInfo))));
    }

    static void sms(Message messenger, CommandSender sendi, String msg, List<Object> placeholderInfo) {
        if (!msg.isEmpty())
            Bukkit.getScheduler().runTask(AmethystGear.getInstance(), () ->
                    sendi.sendMessage(placeholder(sendi, getPrefix(messenger) + msg, placeholderInfo)));
    }

    static void sms(CommandSender sendi, List<String> msg, Object placeholderInfo) {
        if (msg != null && !msg.isEmpty()) {
            Bukkit.getScheduler().runTask(AmethystGear.getInstance(), () -> {
                msg.forEach(str -> msg.set(msg.indexOf(str), placeholder(sendi, str, placeholderInfo)));
                sendi.sendMessage(msg.toArray(new String[0]));
            });
        }
    }

    static void smsActionBar(Player sendi, List<String> msg) {
        if (msg == null || msg.isEmpty()) return;
        String str = msg.get(new Random().nextInt(msg.size()));
        smsActionBar(sendi, str);
    }

    static void smsActionBar(Player sendi, String msg) {
        if (msg == null || msg.isEmpty()) return;
        sendi.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(msg));
    }

    static void smsTitle(Player sendi, List<String> msg) {
        if (msg == null || msg.isEmpty()) return;
        if (msg.size() == 1)
            Audience.audience(sendi).showTitle(Title.title(Component.text(" "), Component.text(msg.get(0))));
        else
            Audience.audience(sendi).showTitle(Title.title(Component.text(msg.get(0)), Component.text(msg.get(1))));
    }

    static String getPrefix(Message messenger) {
        return messenger.lang().getString("Messages.Prefix");
    }

    /**
     * @param info: Accepts String, PersistentDataContainer
     * **/
    static List<String> placeholder(@Nullable CommandSender p, List<String> str, Object info) {
        if (str instanceof ImmutableCollection)
            return str;
        for (int i = 0; i < str.size(); i++) {
            String s = placeholder(p, str.get(i), info);
            if (s != null)
                str.set(i, s);
            else {
                str.remove(i);
                i--;
            }
        }
        //str.forEach(s -> str.set(str.indexOf(s), placeholder(p, s, info)));
        return str;
    }

    @Nullable
    static String placeholder(@Nullable CommandSender p, String str, @Nullable Object info) {
        if (info instanceof Collection<?>)
            str = placeholder(p, str, Collections.unmodifiableList((List<?>) info));
        else if (str != null)
            str = PlaceholderAnalyzer.applyPlaceholders(p, str, info);
        if (str != null)
            return color(str);
        return null;
    }

    static String placeholder(@Nullable CommandSender p, String str, @NonNull List<Object> info) {
        for (Object obj : info)
            str = placeholder(p, str, obj);
        return str;
    }

    static String placeholder(@Nullable CommandSender p, String str) {
        if (str != null)
            str = PlaceholderAnalyzer.applyPlaceholders(p, str, null);
        if (str != null)
            return color(str);
        return null;
    }

    static String color(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        return translateHexColorCodes(str);
    }

    char COLOR_CHAR = ChatColor.COLOR_CHAR;

    private static String translateHexColorCodes(String message)
    {
        final Pattern hexPattern = Pattern.compile("&#" + "([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
