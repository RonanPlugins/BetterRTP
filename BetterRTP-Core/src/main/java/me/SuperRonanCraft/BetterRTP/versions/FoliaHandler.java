package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

public class FoliaHandler {

    private ServerImplementation SERVER_IMPLEMENTATION;

    public void load() {
        this.SERVER_IMPLEMENTATION = new FoliaLib(BetterRTP.getInstance()).getImpl();
    }

    public ServerImplementation get() {
        return SERVER_IMPLEMENTATION;
    }

}
