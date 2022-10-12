package me.SuperRonanCraft.BetterRTP.references.messages;

import me.SuperRonanCraft.BetterRTP.references.file.FileData;

public enum MessagesHelp implements MessageData {

    PREFIX("Prefix"),
    MAIN("Main"),
    BIOME("Biome"),
    EDIT("Edit"),
    HELP("Help"),
    INFO("Info"),
    PLAYER("Player"),
    RELOAD("Reload"),
    SETTINGS("Settings"),
    TEST("Test"),
    VERSION("Version"),
    WORLD("World"),
    LOCATION("Location"),
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
