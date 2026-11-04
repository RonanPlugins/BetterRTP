package me.SuperRonanCraft.BetterRTP.references.web.editor;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/** Independent config uploads; HTTP runs on a dedicated worker. No editor lifecycle. */
public final class EditorBridge {
    private static final String WEBSITE = loadWebsite();

    private static String loadWebsite() {
        Properties metadata = new Properties();
        try (InputStream input = EditorBridge.class.getResourceAsStream("/META-INF/betterrtp-editor.properties")) {
            if (input == null) throw new IllegalStateException("Missing editor build metadata");
            metadata.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read editor build metadata", e);
        }
        String website = metadata.getProperty("website");
        return website;
    }
    private final JavaPlugin plugin;
    private final Consumer<Runnable> sync;
    private final Consumer<CommandSender> reload;
    private final Predicate<CommandSender> allowed;
    private final Supplier<String> loadedConfig;
    private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "BetterRTP-editor"); thread.setDaemon(true); return thread;
    });
    private volatile boolean applying;
    private volatile boolean stopped;

    public EditorBridge(JavaPlugin plugin, Consumer<Runnable> sync, Consumer<CommandSender> reload,
                        Predicate<CommandSender> allowed, Supplier<String> loadedConfig) {
        this.plugin = plugin;
        this.sync = sync;
        this.reload = reload;
        this.allowed = allowed;
        this.loadedConfig = loadedConfig;
        // Saved versions are fetched only by an explicit editorsync command.
    }
    public void close() { stopped = true; worker.shutdownNow(); }

    public void upload(CommandSender sender) {
        if (!allowed.test(sender)) return;
        try {
            byte[] bytes = EditorConfig.read(configPath());
            String content = new String(bytes, StandardCharsets.UTF_8); EditorConfig.parse(content);
            Upload upload = new Upload(sender instanceof Player ? ((Player) sender).getUniqueId() : null);
            // Snapshot loaded worlds on the command thread, before starting HTTP work.
            List<String> worlds = new ArrayList<>();
            for (org.bukkit.World world : Bukkit.getWorlds()) worlds.add(world.getName());
            sender.sendMessage("§7Uploading a private BetterRTP editor config…");
            worker.execute(() -> {
                try {
                    JsonObject body = new JsonObject(); body.addProperty("protocol", 4);
                    body.addProperty("playerId", upload.player == null ? null : upload.player.toString());
                    body.addProperty("configFile", "config.yml");
                    JsonArray availableWorlds = new JsonArray();
                    for (String world : worlds) availableWorlds.add(new JsonPrimitive(world));
                    body.add("worlds", availableWorlds);
                    body.addProperty("version", plugin.getDescription().getVersion()); body.addProperty("content", content);
                    JsonObject result = request("POST", "/api/editor/tokens", body, null);
                    String browserToken = result.get("token").getAsString();
                    if (!browserToken.matches("[A-Za-z0-9_-]{8}")) throw new IOException("Invalid relay response.");
                    if (stopped) return;
                    sync.accept(() -> {
                        if (stopped) return;
                        CommandSender recipient = recipient(upload);
                        if (recipient == null || !allowed.test(recipient)) return;
                        String url = WEBSITE + "/editor/betterrtp?token=" + browserToken;
                        recipient.sendMessage("§ePrivate editor link: expires in 24 hours. Save online, then run the sync command here. Do not share it.");
                        if (recipient instanceof Player) {
                            TextComponent link = new TextComponent("§a[Open BetterRTP editor]");
                            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                            ((Player) recipient).spigot().sendMessage(link);
                        } else recipient.sendMessage(url);
                    });
                } catch (Exception error) {
                    plugin.getLogger().warning("Editor upload failed (" + error.getClass().getSimpleName() + "). Check website/database availability.");
                    message(upload, "Could not upload the editor config. " + safeError(error));
                }
            });
        } catch (Exception error) { sender.sendMessage("§cEditor: " + error.getMessage()); }
    }

    private Path configPath() { return plugin.getDataFolder().toPath().resolve("config.yml"); }
    private CommandSender recipient(Upload upload) { return upload.player == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(upload.player); }
    private void message(Upload upload, String text) {
        if (!stopped) sync.accept(() -> {
            CommandSender sender = recipient(upload); if (sender != null) sender.sendMessage("§eEditor: " + text);
        });
    }
    private void message(UUID playerId, String text) {
        if (!stopped) sync.accept(() -> {
            CommandSender player = playerId == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(playerId);
            if (player != null) player.sendMessage("§eEditor: " + text);
        });
    }
    public void redeem(CommandSender sender, String token) {
        if (!allowed.test(sender)) return;
        if (!token.matches("[A-Za-z0-9_-]{8}")) { sender.sendMessage("§cInvalid sync token. Copy the command from the editor."); return; }
        if (applying) { sender.sendMessage("§eAn editor sync is already in progress."); return; }
        final UUID executor = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        applying = true;
        sender.sendMessage("§7Downloading your saved configuration…");
        worker.execute(() -> {
            try {
                JsonObject response = request("GET", "/api/editor/sync", null, token);
                if (!"config.yml".equals(response.get("configFile").getAsString())) throw new IOException("Invalid sync target.");
                String edited = response.get("content").getAsString();
                long expires = response.get("expiresAt").getAsLong();
                // Public commands can come from any editor; validate against this server, not a browser baseline.
                String original = new String(EditorConfig.read(configPath()), StandardCharsets.UTF_8);
                EditorConfig.validate(original, edited);
                sync.accept(() -> apply(original, edited, expires, executor));
            } catch (Exception error) {
                applying = false;
                message(executor, "Sync failed: " + safeError(error) + " Save online again for a new command if this token was consumed.");
            }
        });
    }

    private static String safeError(Exception error) {
        // Do not log URLs, headers, raw responses, tokens, or config contents.
        if (error instanceof IOException && error.getMessage() != null && error.getMessage().startsWith("Relay HTTP "))
            return error.getMessage();
        return "Request or validation failed (" + error.getClass().getSimpleName() + ").";
    }

    private void apply(String original, String edited, long tokenExpires, UUID executor) {
        try {
            if (stopped) return;
            if (System.currentTimeMillis() >= tokenExpires) throw new IOException("Sync token expired before apply.");
            CommandSender sender = executor == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(executor);
            if (sender == null || !allowed.test(sender)) throw new IOException("The syncing player must be online and still have betterrtp.editor.");
            Path backup = EditorConfig.apply(configPath(), original.getBytes(StandardCharsets.UTF_8), edited, () -> {
                reload.accept(sender);
                String loaded = loadedConfig.get();
                String persisted = new String(EditorConfig.read(configPath()), StandardCharsets.UTF_8);
                if (!EditorConfig.equivalent(persisted, loaded))
                    throw new IOException("Loaded configuration does not match the saved settings.");
            });
            sender.sendMessage("§aEditor changes applied and reloaded. Backup: " + backup.getFileName());
        } catch (EditorConfig.ReloadFailure error) {
            message(executor, "Configuration saved, but reload failed. The new file remains in place. Check the console and reload BetterRTP.");
            plugin.getLogger().warning("Editor config was written but reload failed; no rollback was performed.");
        } catch (Exception error) {
            message(executor, "Sync not applied: " + error.getMessage());
        } finally { applying = false; }
    }

    private JsonObject request(String method, String path, JsonObject body, String syncToken) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(WEBSITE + path).openConnection();
        connection.setConnectTimeout(5000); connection.setReadTimeout(10000); connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method); connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("X-Editor-Actor", "plugin");
        connection.setRequestProperty("User-Agent", "BetterRTP-Editor/4");
        connection.setRequestProperty("X-Editor-Protocol", "4");
        if (syncToken != null) {
            connection.setRequestProperty("X-Editor-Sync-Token", syncToken);
        }
        try {
            if (body != null) {
                byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
                connection.setDoOutput(true); connection.setFixedLengthStreamingMode(bytes.length);
                connection.setRequestProperty("Content-Type", "application/json");
                try (OutputStream out = connection.getOutputStream()) { out.write(bytes); }
            }
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) throw new IOException("Relay HTTP " + status);
            try (InputStream in = connection.getInputStream(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] chunk = new byte[8192]; int count;
                while ((count = in.read(chunk)) != -1) {
                    if (buffer.size() + count > EditorConfig.MAX_BYTES * 6 + 4096) throw new IOException("Relay response too large.");
                    buffer.write(chunk, 0, count);
                }
                return new JsonParser().parse(new String(buffer.toByteArray(), StandardCharsets.UTF_8)).getAsJsonObject();
            }
        } finally { connection.disconnect(); }
    }
    private static final class Upload {
        final UUID player;
        Upload(UUID player) { this.player = player; }
    }
}
