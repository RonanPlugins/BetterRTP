package me.SuperRonanCraft.BetterRTP.references.messages;

import me.SuperRonanCraft.BetterRTP.references.file.FileData;
import me.ronancraft.AmethystGear.resources.files.FileData;

public interface MessageData {

    String section();

    String prefix();

    FileData file();

    default String get() {
        return file().getString(prefix() + section());
    }
}
