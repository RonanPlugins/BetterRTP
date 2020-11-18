package me.SuperRonanCraft.BetterRTPAddons;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTPAddons.util.LangFile;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface AddonsMessages {

    default LangFile getLang() {
        return Main.getInstance().getFiles().getLang();
    }

    default void sms(CommandSender sendi, String msg) {
        BetterRTP.getInstance().getText().sms(sendi, msg);
    }

    default void sms(CommandSender sendi, List<String> msg) {
        BetterRTP.getInstance().getText().sms(sendi, msg);
    }

    default String color(String str) {
        return BetterRTP.getInstance().getText().color(str);
    }

    default String colorPre(String str) {
        return BetterRTP.getInstance().getText().colorPre(str);
    }
}
