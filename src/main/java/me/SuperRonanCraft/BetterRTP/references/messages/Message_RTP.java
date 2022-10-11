package me.SuperRonanCraft.BetterRTP.references.messages;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.ronancraft.AmethystGear.AmethystGear;
import me.ronancraft.AmethystGear.resources.files.FileData;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Message_RTP implements Message {

    static Message_RTP msg = new Message_RTP();

    public static FileData getLang() {
        return BetterRTP.getInstance().getFiles().getLang();
    }

    @Override
    public FileData lang() {
        return getLang();
    }

    public static void sms(CommandSender sendi, String msg) {
        Message.sms(Message_Gear.msg, sendi, msg);
    }

    public static void sms(CommandSender sendi, String msg, Object placeholderInfo) {
        Message.sms(Message_Gear.msg, sendi, msg, placeholderInfo);
    }

    public static void sms(CommandSender sendi, String msg, List<Object> placeholderInfo) {
        Message.sms(Message_Gear.msg, sendi, msg, placeholderInfo);
    }

    public static String getPrefix() {
        return Message.getPrefix(Message_Gear.msg);
    }
}
