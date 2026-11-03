package me.SuperRonanCraft.BetterRTP.references.web.editor;

import com.google.gson.JsonObject;
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

/** One active session per server, all HTTP on a dedicated daemon worker. No inbound server port. */
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
        if (!"https://minecentral.net".equals(website)
                && !"https://n2nm1thopoojau1ggnalixvr.minecentral.net".equals(website)) {
            throw new IllegalStateException("Invalid editor website in build metadata");
        }
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
    private volatile Session current;
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
    public void close() { stopped = true; current = null; worker.shutdownNow(); }

    public void open(CommandSender sender) {
        if (!allowed.test(sender)) return;
        if (!(sender instanceof Player)) { sender.sendMessage("§cEditor sessions must be created by a player."); return; }
        try {
            if (current != null && System.currentTimeMillis() >= current.expires + 15 * 60 * 1000) current = null;
            if (current != null) throw new IOException("An editor is already active. Use /rtp editor cancel first.");
            byte[] bytes = EditorConfig.read(configPath());
            String content = new String(bytes, StandardCharsets.UTF_8); EditorConfig.parse(content);
            Session session = new Session(content, bytes, sender instanceof Player ? ((Player) sender).getUniqueId() : null);
            final String serverName = Bukkit.getServer().getServerName();
            current = session;
            sender.sendMessage("§7Creating a private BetterRTP editor session…");
            worker.execute(() -> {
                try {
                    JsonObject body = new JsonObject(); body.addProperty("protocol", 2);
                    body.addProperty("playerId", session.player.toString());
                    body.addProperty("serverName", serverName);
                    body.addProperty("configFile", "config.yml");
                    body.addProperty("version", plugin.getDescription().getVersion()); body.addProperty("content", content);
                    JsonObject result = request(session, "POST", "/api/editor/sessions", body);
                    session.pluginToken = result.get("pluginToken").getAsString();
                    String browserToken = result.get("browserToken").getAsString();
                    if (!session.pluginToken.matches("[a-f0-9]{64}") || !browserToken.matches("[a-f0-9]{64}")) throw new IOException("Invalid relay response.");
                    session.expires = Math.min(session.expires, result.get("expiresAt").getAsLong());
                    if (current != session || stopped) { revoke(session); return; }
                    sync.accept(() -> {
                        if (current != session || stopped) return;
                        CommandSender recipient = recipient(session);
                        if (recipient == null || !allowed.test(recipient)) { cancelSession(session); return; }
                        String url = WEBSITE + "/editor/session#" + browserToken;
                        recipient.sendMessage("§ePrivate editor link: expires in 24 hours. Save online, then run the sync command here. Do not share it.");
                        if (recipient instanceof Player) {
                            TextComponent link = new TextComponent("§a[Open BetterRTP editor]");
                            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                            ((Player) recipient).spigot().sendMessage(link);
                        } else recipient.sendMessage(url);
                    });
                } catch (Exception error) {
                    if (current == session) current = null;
                    plugin.getLogger().warning("Editor session creation failed (" + error.getClass().getSimpleName() + "). Check website/database availability.");
                    message(session, "Could not create an editor session. " + safeError(error));
                }
            });
        } catch (Exception error) { sender.sendMessage("§cEditor: " + error.getMessage()); }
    }

    public void cancel(CommandSender sender) {
        if (!allowed.test(sender)) return;
        Session session = current;
        if (session != null && (!(sender instanceof Player) || !session.player.equals(((Player) sender).getUniqueId()))) {
            sender.sendMessage("§cOnly the player who created the editor session can cancel it."); return;
        }
        if (session != null) cancelSession(session);
        sender.sendMessage("§7Editor session cancelled. Changes already applied cannot be undone by cancelling.");
    }
    private void cancelSession(Session session) {
        if (current == session) current = null;
        if (!stopped) worker.execute(() -> revoke(session));
    }
    private void revoke(Session session) {
        if (session.pluginToken != null) try { request(session, "DELETE", "/api/editor/session", null); } catch (Exception ignored) { }
    }
    private Path configPath() { return plugin.getDataFolder().toPath().resolve("config.yml"); }
    private CommandSender recipient(Session session) { return session.player == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(session.player); }
    private void message(Session session, String text) {
        if (!stopped) sync.accept(() -> {
            CommandSender sender = recipient(session); if (sender != null) sender.sendMessage("§eEditor: " + text);
        });
    }
    public void redeem(CommandSender sender, String token) {
        if (!allowed.test(sender)) return;
        Session session = current;
        if (!(sender instanceof Player) || session == null || !session.player.equals(((Player) sender).getUniqueId())) {
            sender.sendMessage("§cOnly the player who created this server's editor session can sync it."); return;
        }
        if (!token.matches("[a-f0-9]{64}")) { sender.sendMessage("§cInvalid sync token. Copy the command from the editor."); return; }
        if (session.pluginToken == null || session.applying) { sender.sendMessage("§eAn editor request is already in progress."); return; }
        if (System.currentTimeMillis() >= session.expires + 15 * 60 * 1000) {
            sender.sendMessage("§cSession expired. Run /rtp editor again."); return;
        }
        session.applying = true;
        sender.sendMessage("§7Downloading your saved configuration…");
        worker.execute(() -> {
            try {
                JsonObject response = request(session, "GET", "/api/editor/sync", null, token);
                if (!session.player.toString().equals(response.get("playerId").getAsString()) ||
                        !"config.yml".equals(response.get("configFile").getAsString())) throw new IOException("Invalid sync target.");
                String edited = response.get("content").getAsString();
                long expires = response.get("expiresAt").getAsLong();
                EditorConfig.validate(session.original, edited);
                sync.accept(() -> apply(session, edited, expires));
            } catch (Exception error) {
                session.applying = false;
                message(session, "Sync failed: " + safeError(error) + " Save online again for a new command if this token was consumed.");
            }
        });
    }

    private static String safeError(Exception error) {
        // Do not log URLs, headers, raw responses, tokens, or config contents.
        if (error instanceof IOException && error.getMessage() != null && error.getMessage().startsWith("Relay HTTP "))
            return error.getMessage();
        return "Request or validation failed (" + error.getClass().getSimpleName() + ").";
    }

    private void apply(Session session, String edited, long tokenExpires) {
        try {
            if (stopped || current != session) return;
            if (System.currentTimeMillis() >= tokenExpires) throw new IOException("Sync token expired before apply.");
            CommandSender sender = recipient(session);
            if (sender == null || !allowed.test(sender)) throw new IOException("The session creator must be online and still have betterrtp.editor.");
            Path backup = EditorConfig.apply(configPath(), session.bytes, edited, () -> {
                reload.accept(sender);
                String loaded = loadedConfig.get();
                String persisted = new String(EditorConfig.read(configPath()), StandardCharsets.UTF_8);
                if (!EditorConfig.equivalent(persisted, loaded))
                    throw new IOException("Loaded configuration does not match the saved settings.");
            });
            sender.sendMessage("§aEditor changes applied and reloaded. Backup: " + backup.getFileName());
        } catch (EditorConfig.ReloadFailure error) {
            message(session, "Configuration saved, but reload failed. The new file remains in place. Check the console and reload BetterRTP.");
            plugin.getLogger().warning("Editor config was written but reload failed; no rollback was performed.");
        } catch (Exception error) {
            message(session, "Sync not applied: " + error.getMessage());
        } finally { session.applying = false; }
    }

    private JsonObject request(Session session, String method, String path, JsonObject body) throws IOException {
        return request(session, method, path, body, null);
    }
    private JsonObject request(Session session, String method, String path, JsonObject body, String syncToken) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(WEBSITE + path).openConnection();
        connection.setConnectTimeout(5000); connection.setReadTimeout(10000); connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method); connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("X-Editor-Actor", "plugin");
        connection.setRequestProperty("User-Agent", "BetterRTP-Editor/2");
        if (syncToken != null) {
            connection.setRequestProperty("X-Editor-Sync-Token", syncToken);
            connection.setRequestProperty("X-Editor-Player", session.player.toString());
        }
        if (session.pluginToken != null) connection.setRequestProperty("Authorization", "Bearer " + session.pluginToken);
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
    private static final class Session {
        final String original; final byte[] bytes; final UUID player;
        volatile String pluginToken;
        volatile boolean applying;
        volatile long expires = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        Session(String original, byte[] bytes, UUID player) { this.original = original; this.bytes = bytes; this.player = player; }
    }
}
