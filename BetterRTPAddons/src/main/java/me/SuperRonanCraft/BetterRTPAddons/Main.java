package me.SuperRonanCraft.BetterRTPAddons;

import com.ronanplugins.bettersync.editor.EditorClient;
import com.ronanplugins.bettersync.editor.EditorSchema;

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
    private EditorClient editorBridge;

    public boolean canEdit(org.bukkit.command.CommandSender sender) {
        return !(sender instanceof org.bukkit.entity.Player) || sender.hasPermission("betterrtp.addon.editor");
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(reloadListener, this);
        load();
        editorBridge = new EditorClient(
                this, EditorSchema.load(this, "META-INF/editor-schema.json"),
                EditorClient.website(this, "META-INF/betterrtp-addons-editor.properties"),
                me.SuperRonanCraft.BetterRTP.versions.AsyncHandler::sync, sender -> load(),
                this::canEdit, () -> Files.FILETYPE.CONFIG.getConfig().saveToString());
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
