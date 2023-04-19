package me.SuperRonanCraft.BetterRTP.references.helpers;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

public class FoliaHelper {

    public static ServerImplementation get() {
        return BetterRTP.getInstance().getFolia().getImpl();
    }

}
