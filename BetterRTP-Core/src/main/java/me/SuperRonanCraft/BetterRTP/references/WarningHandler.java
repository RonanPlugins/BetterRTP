package me.SuperRonanCraft.BetterRTP.references;

import me.SuperRonanCraft.BetterRTP.BetterRTP;

import java.util.HashMap;

public class WarningHandler {

    HashMap<WARNING, Long> lastWarning = new HashMap<>();

    public static void warn(WARNING type, String str) {
        warn(type, str, true);
    }

    public static void warn(WARNING type, String str, boolean auto_ignore) {
        WarningHandler handler = BetterRTP.getInstance().getWarningHandler();
        if (auto_ignore) { //Ignored automatically every 30 minutes
            Long lastTime = handler.lastWarning.getOrDefault(type, 0L);
            if (lastTime <= System.currentTimeMillis()) {
                BetterRTP.getInstance().getLogger().info(str);
                lastTime += System.currentTimeMillis() + (1000 * 1800);
            }
            handler.lastWarning.put(type, lastTime);
        } else
            BetterRTP.getInstance().getLogger().warning(str);
    }

    public enum WARNING {
        USELOCATION_ENABLED_NO_LOCATION_AVAILABLE,
        NO_WORLD_TYPE_DECLARED
    }
}
