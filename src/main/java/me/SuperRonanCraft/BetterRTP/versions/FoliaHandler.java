package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

public class FoliaHandler {

    private volatile ServerImplementation SERVER_IMPLEMENTATION;

    public synchronized void load() {
        if (SERVER_IMPLEMENTATION == null)
            SERVER_IMPLEMENTATION = new FoliaLib(BetterRTP.getInstance()).getImpl();
    }

    public ServerImplementation get() {
        return SERVER_IMPLEMENTATION;
    }

}
