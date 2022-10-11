package me.SuperRonanCraft.BetterRTP.references.messages;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

public enum MessagesCore {
    RELOAD("Reload"),
    NOPERMISSION("NoPermission"),
    INVALID("Invalid"),
    INVALID_MATERIAL("InvalidMaterial"),
    ERROR("DatabaseError"),
    PLAYER_DOESNT_EXIST("PlayerExist"),
    UNSUPPORTED_MOB("UnsupportedMob"),
    //GEAR
    GEAR_GIVEN("Gear.Given"),
    GEAR_EQUIPPED("Gear.Equipped"),
    GEAR_EQUIPPED_FULL("Gear.FullInventory"),
    GEAR_UNEQUIPPED("Gear.Unequipped"),
    GEAR_UPGRADED("Gear.Upgraded"),
    GEAR_CATALYST_UPGRADED("Gear.Catalyst-Upgraded"),
    //CATALYSTS
    CATALYST_GIVEN("Catalyst.Given"),
    CATALYST_ADDED("Catalyst.Added"),
    CATALYST_REMOVED("Catalyst.Removed"),
    CATALYST_REMOVED_SHATTERED("Catalyst.Removed-Shattered"),
    CATALYST_UPGRADED("Catalyst.Upgraded"),
    //COINS
    COINS_GIVEN("Coins.Given"),
    COINS_NOTENOUGH("Coins.NotEnough"),
    //AMETHYSTS
    AMETHSYT_GIVEN("Amethyst.Given"),
    AMETHSYT_NOTENOUGH("Amethyst.NotEnough"),
    //GEODES
    GEODE_GIVEN("Geodes.Given"),
    GEODE_FRAGMENT_GIVEN("Geodes.Fragments.Given"),
    //TRACKERS
    TRACKER_GIVEN("Tracker.Given"),
    TRACKER_ADDED("Tracker.Added"),
    //SHOP
    SHOP_LINK("Shop.Link"),
    ;

    final String section;

    MessagesCore(String section) {
        this.section = section;
    }

    private static final String pre = "Messages.";

    public void send(CommandSender sendi) {
        Message_Gear.sms(sendi, Message_Gear.getLang().getString(pre + section));
    }

    public void send(CommandSender sendi, Object placeholderInfo) {
        Message_Gear.sms(sendi, Message_Gear.getLang().getString(pre + section), placeholderInfo);
    }

    public void send(CommandSender sendi, List<Object> placeholderInfo) {
        Message_Gear.sms(sendi, Message_Gear.getLang().getString(pre + section), placeholderInfo);
    }

    public String get(CommandSender p, Object placeholderInfo) {
        return Message.placeholder(p, Message_Gear.getLang().getString(pre + section), placeholderInfo);
    }

    public void send(CommandSender sendi, HashMap<String, String> placeholder_values) {
        String msg = Message_Gear.getLang().getString(pre + section);
        for (String ph : placeholder_values.values())
            msg = msg.replace(ph, placeholder_values.get(ph));
        Message_Gear.sms(sendi, msg);
    }
}
