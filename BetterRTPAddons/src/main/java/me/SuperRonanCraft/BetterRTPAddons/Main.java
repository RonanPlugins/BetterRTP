package me.SuperRonanCraft.BetterRTPAddons;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import me.SuperRonanCraft.BetterRTPAddons.util.ReloadListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private final AddonsHandler addonsHandler = new AddonsHandler();
    @Getter
    private final Files files = new Files();
    public final AddonMessages msgs = new AddonMessages();
    private final ReloadListener reloadListener = new ReloadListener();

    @Getter
    private me.SuperRonanCraft.BetterRTP.references.web.editor.EditorBridge editorBridge;

    public boolean canEdit(org.bukkit.command.CommandSender sender) {
        return !(sender instanceof org.bukkit.entity.Player) || sender.hasPermission("betterrtp.addon.editor");
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(reloadListener, this);
        load();
        editorBridge = new me.SuperRonanCraft.BetterRTP.references.web.editor.EditorBridge(
                this, me.SuperRonanCraft.BetterRTP.versions.AsyncHandler::sync, sender -> load(),
                this::canEdit, () -> Files.FILETYPE.CONFIG.getConfig().saveToString(), "betterrtp-addons");
    }

    @Override
    public void onDisable() {
        if (editorBridge != null) editorBridge.close();
        addonsHandler.unload();
    }

    public void load() {
        files.load();
        addonsHandler.load();
    }

}
