package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

public class FoliaHandler {

    public static ServerImplementation get() {
        return new FoliaLib(BetterRTP.getInstance()).getImpl();
    }

}
