package me.SuperRonanCraft.BetterRTP.references.messages;

import me.SuperRonanCraft.BetterRTP.references.file.FileData;

public enum MessagesHelp implements MessageData {

    PREFIX("Prefix"),
    //Amethyst
    AMETHYST_ADMIN("Admin"),
    AMETHYST_CATALOG("Catalog"),
    AMETHYST_CATALYSTS("Catalysts"),
    AMETHYST_GEAR("Gear"),
    AMETHYST_GEODES("Geodes"),
    AMETHYST_GIVE("Give"),
    AMETHYST_PROFILE("Profile"),
    AMETHYST_SHOP("Shop"),
    AMETHYST_XPBOOST("XPBoost"),
    //Base
    RELOAD("Reload"),
    HELP("Help"),
    ;

    final String section;

    MessagesHelp(String section) {
        this.section = section;
    }

    @Override
    public String prefix() {
        return "Help.";
    }

    @Override
    public FileData file() {
        return Message_RTP.getLang();
    }

    @Override
    public String section() {
        return section;
    }
}
