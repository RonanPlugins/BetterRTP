package me.SuperRonanCraft.BetterRTP.references.messages;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

public enum MessagesCore {

    SUCCESS_PAID("Success.Paid"),
    SUCCESS_BYPASS("Success.Bypass"),
    SUCCESS_LOADING("Success.Loading"),
    SUCCESS_TELEPORT("Success.Teleport"),
    FAILED_NOTSAFE("Failed.NotSafe"),
    FAILED_PRICE("Failed.Price"),
    FAILED_HUNGER("Failed.Hunger"),
    OTHER_NOTSAFE("Other.NotSafe"),
    OTHER_SUCCESS("Other.Success"),
    OTHER_BIOME("Other.Biome"),
    NOTEXIST("NotExist"),
    RELOAD("Reload"),
    NOPERMISSION("NoPermission.Basic"),
    NOPERMISSION_WORLD("NoPermission.World"),
    DISABLED_WORLD("DisabledWorld"),
    COOLDOWN("Cooldown"),
    INVALID("Invalid"),
    NOTONLINE("NotOnline"),
    DELAY("Delay"),
    SIGN("Sign"),
    MOVED("Moved"),
    ALREADY("Already"),
    PREFIX("Prefix"),
    //EDIT
    EDIT_ERROR("Edit.Error"),
    EDIT_SET("Edit.Set"),
    EDIT_REMOVE("Edit.Remove"),
    ;

    final String section;

    MessagesCore(String section) {
        this.section = section;
    }

    private static final String pre = "Messages.";

    public void send(CommandSender sendi) {
        Message_RTP.sms(sendi, Message_RTP.getLang().getString(pre + section));
    }

    public void send(CommandSender sendi, Object placeholderInfo) {
        Message_RTP.sms(sendi, Message_RTP.getLang().getString(pre + section), placeholderInfo);
    }

    public void send(CommandSender sendi, List<Object> placeholderInfo) {
        Message_RTP.sms(sendi, Message_RTP.getLang().getString(pre + section), placeholderInfo);
    }

    public String get(CommandSender p, Object placeholderInfo) {
        return Message.placeholder(p, Message_RTP.getLang().getString(pre + section), placeholderInfo);
    }

    public void send(CommandSender sendi, HashMap<String, String> placeholder_values) {
        String msg = Message_RTP.getLang().getString(pre + section);
        for (String ph : placeholder_values.values())
            msg = msg.replace(ph, placeholder_values.get(ph));
        Message_RTP.sms(sendi, msg);
    }
}
