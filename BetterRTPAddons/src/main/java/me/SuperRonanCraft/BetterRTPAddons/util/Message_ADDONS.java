package me.SuperRonanCraft.BetterRTPAddons.util;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileData;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Message_ADDONS implements Message {

    public static Message_ADDONS msg = new Message_ADDONS();

    public static FileData getLang() {
        return BetterRTP.getInstance().getFiles().getLang();
    }

    @Override
    public FileData lang() {
        return getLang();
    }

    public static void sms(CommandSender sendi, String msg) {
        Message.sms(Message_ADDONS.msg, sendi, msg);
    }

    public static void sms(CommandSender sendi, String msg, Object placeholderInfo) {
        Message.sms(Message_ADDONS.msg, sendi, msg, placeholderInfo);
    }

    public static void sms(CommandSender sendi, String msg, List<Object> placeholderInfo) {
        Message.sms(Message_ADDONS.msg, sendi, msg, placeholderInfo);
    }

    public static void sms(CommandSender sendi, List<String> msg, List<Object> placeholderInfo) {
        Message.sms(sendi, msg, placeholderInfo);
    }

    public static String getPrefix() {
        return Message.getPrefix(Message_ADDONS.msg);
    }
}
