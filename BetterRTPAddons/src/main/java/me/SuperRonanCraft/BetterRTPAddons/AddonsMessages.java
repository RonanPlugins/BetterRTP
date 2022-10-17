package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTPAddons.util.LangFile;
import me.SuperRonanCraft.BetterRTPAddons.util.Message_ADDONS;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface AddonsMessages {

    default LangFile getLang() {
        return Main.getInstance().getFiles().getLang();
    }

    default void sms(CommandSender sendi, String msg) {
        Message_RTP.sms(sendi, msg);
    }

    default void sms(CommandSender sendi, List<String> msg) {
        Message_ADDONS.sms(sendi, msg, null);
    }

    default String color(String str) {
        return Message.color(str);
    }
}
