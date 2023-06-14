package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

public class FoliaHandler {

    private static final ServerImplementation SERVER_IMPLEMENTATION = new FoliaLib(BetterRTP.getInstance()).getImpl();

    public static ServerImplementation get() {
        return SERVER_IMPLEMENTATION;
    }

}
