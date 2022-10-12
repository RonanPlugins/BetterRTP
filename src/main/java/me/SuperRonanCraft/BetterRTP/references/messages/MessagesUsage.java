package me.SuperRonanCraft.BetterRTP.references.messages;

import me.SuperRonanCraft.BetterRTP.references.file.FileData;
import org.bukkit.command.CommandSender;

public enum MessagesUsage implements MessageData {

    GIVE_GEAR("Give.Gear"),
    GIVE_CATALYST("Give.Catalyst"),
    GIVE_TRACKER("Give.Tracker"),
    GIVE_COINS("Give.Coins"),
    GIVE_AMETHYSTS("Give.Amethysts"),
    GIVE_GEODES("Give.Geodes"),
    XPBOOST_PLAYER("XpBoost.Player"),
    XPBOOST_ONLINE("XpBoost.Online"),
    XPBOOST_GLOBAL("XpBoost.Global"),
    SPAWN_MOB("SpawnMob"),
    RELOAD("Reload"),
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
