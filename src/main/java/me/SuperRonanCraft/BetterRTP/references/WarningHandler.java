package me.SuperRonanCraft.BetterRTP.references;

import me.SuperRonanCraft.BetterRTP.BetterRTP;

import java.util.HashMap;

public class WarningHandler {

    HashMap<WARNING, Long> lastWarning = new HashMap<>();

    public static void warn(WARNING type, String str) {
        WarningHandler handler = BetterRTP.getInstance().getWarningHandler();
        Long lastTime = handler.lastWarning.getOrDefault(type, 0L);
        if (lastTime <= System.currentTimeMillis()) {
            BetterRTP.getInstance().getLogger().info(str);
            lastTime += System.currentTimeMillis() + (1000 * 120);
        }
        handler.lastWarning.put(type, lastTime);
    }

    public enum WARNING {
        USELOCATION_ENABLED_NO_LOCATION_AVAILABLE
    }
}
