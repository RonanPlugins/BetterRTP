package me.SuperRonanCraft.BetterRTP.references.messages;

import me.SuperRonanCraft.BetterRTP.references.file.FileData;
import org.bukkit.command.CommandSender;

public enum MessagesUsage implements MessageData {

    RTP_OTHER("Player"),
    WORLD("World"),
    BIOME("Biome"),
    LOCATION("Location"),
    //Edit
    EDIT_LOCATION("Edit.Location"),
    EDIT_BASE("Edit.Base"),
    EDIT_DEFAULT("Edit.Default"),
    EDIT_WORLD("Edit.World"),
    EDIT_WORLDTYPE("Edit.Worldtype"),
    EDIT_OVERRIDE("Edit.Override"),
    EDIT_BLACKLISTEDBLLOCKS("Edit.BlacklistedBlocks"),
    EDIT_PERMISSIONGROUP("Edit.PermissionGroup"),
    ;

    final String section;

    MessagesUsage(String section) {
        this.section = section;
    }

    public void send(CommandSender sendi, Object placeholderInfo) {
        Message_RTP.sms(sendi, Message_RTP.getLang().getString(prefix() + section), placeholderInfo);
    }

    @Override
    public String prefix() {
        return "Usage.";
    }

    @Override
    public String section() {
        return section;
    }

    @Override
    public FileData file() {
        return Message_RTP.getLang();
    }
}
