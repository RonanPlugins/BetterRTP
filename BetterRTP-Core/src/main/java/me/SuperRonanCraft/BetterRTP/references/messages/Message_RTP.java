package me.SuperRonanCraft.BetterRTP.references.messages;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileData;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Message_RTP implements Message {

    public static Message_RTP msg = new Message_RTP();

    public static FileData getLang() {
        return BetterRTP.getInstance().getFiles().getLang();
    }

    @Override
    public FileData lang() {
        return getLang();
    }

    public static void sms(CommandSender sendi, String msg) {
        Message.sms(Message_RTP.msg, sendi, msg);
    }

    public static void sms(CommandSender sendi, String msg, Object placeholderInfo) {
        Message.sms(Message_RTP.msg, sendi, msg, placeholderInfo);
    }

    public static void sms(CommandSender sendi, String msg, List<Object> placeholderInfo) {
        Message.sms(Message_RTP.msg, sendi, msg, placeholderInfo);
    }

    public static void sms(CommandSender sendi, List<String> msg, List<Object> placeholderInfo) {
        Message.sms(sendi, msg, placeholderInfo);
    }

    public static String getPrefix() {
        return Message.getPrefix(Message_RTP.msg);
    }
}
