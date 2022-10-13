package me.SuperRonanCraft.BetterRTP.references.settings;

import lombok.Getter;
import lombok.Setter;
import me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins.REGIONPLUGINS;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SoftDepends {

    void load() {
        for (REGIONPLUGINS plugin : REGIONPLUGINS.values())
            registerPlugin(plugin);
    }

    public void registerPlugin(REGIONPLUGINS pl) {
        FileOther.FILETYPE config = BetterRTP.getInstance().getFiles().getType(FileOther.FILETYPE.CONFIG);
        String pre = "Settings.Respect.";
        pl.getPlugin().setRespecting(config.getBoolean(pre + pl.getSetting_name()));
        if (pl.getPlugin().isRespecting())
            pl.getPlugin().setEnabled(Bukkit.getPluginManager().isPluginEnabled(pl.getPluginyml_name()));
        if (pl.getPlugin().isRespecting())
            debug("Respecting `" + pl.getSetting_name() + "` was " + (pl.getPlugin().enabled ? "SUCCESSFULLY" : "NOT") + " registered");
    }



    static public class RegionPlugin {
        @Getter @Setter private boolean respecting;
        @Getter @Setter private boolean enabled;
    }

    private void debug(String str) {
        if (BetterRTP.getInstance().getSettings().isDebug())
            BetterRTP.getInstance().getLogger().log(Level.INFO, str);
    }
}
