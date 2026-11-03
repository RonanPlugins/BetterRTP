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
        worker.scheduleWithFixedDelay(this::poll, 3, 3, TimeUnit.SECONDS);
    }
    public void close() { stopped = true; current = null; worker.shutdownNow(); }

    public void open(CommandSender sender) {
        if (!allowed.test(sender)) return;
        try {
            if (current != null) throw new IOException("An editor is already active. Use /rtp editor cancel first.");
            byte[] bytes = EditorConfig.read(configPath());
            String content = new String(bytes, StandardCharsets.UTF_8); EditorConfig.parse(content);
            Session session = new Session(content, bytes, sender instanceof Player ? ((Player) sender).getUniqueId() : null);
            current = session;
            sender.sendMessage("§7Creating a private BetterRTP editor session…");
            worker.execute(() -> {
                try {
                    JsonObject body = new JsonObject(); body.addProperty("protocol", 1);
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
                        recipient.sendMessage("§ePrivate editor link: expires in 15 minutes, one save. Do not share it.");
                        if (recipient instanceof Player) {
                            TextComponent link = new TextComponent("§a[Open BetterRTP editor]");
                            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                            ((Player) recipient).spigot().sendMessage(link);
                        } else recipient.sendMessage(url);
                    });
                } catch (Exception error) {
                    if (current == session) current = null;
                    message(session, "Could not reach the MineCentral editor. Please try again later.");
                }
            });
        } catch (Exception error) { sender.sendMessage("§cEditor: " + error.getMessage()); }
    }

    public void cancel(CommandSender sender) {
        if (!allowed.test(sender)) return;
        Session session = current;
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
    private void poll() {
        Session session = current;
        if (stopped || session == null || session.pluginToken == null || session.applying) return;
        if (System.currentTimeMillis() >= session.expires) { current = null; revoke(session); message(session, "Session expired. Run /rtp editor again."); return; }
        try {
            if (session.result != null) {
                JsonObject ack = new JsonObject(); ack.addProperty("state", session.result);
                request(session, "POST", "/api/editor/result", ack);
                if (current == session) current = null;
                return;
            }
            JsonObject response = request(session, "GET", "/api/editor/session", null);
            if ("pending".equals(response.get("state").getAsString())) {
                String edited = response.get("content").getAsString();
                try { EditorConfig.validate(session.original, edited); }
                catch (Exception invalid) {
                    session.result = "rejected";
                    message(session, "Configuration rejected: " + invalid.getMessage());
                    return;
                }
                session.applying = true;
                sync.accept(() -> apply(session, edited));
            }
        } catch (Exception error) {
            // Network failures are retried until expiry; never reapply after a recorded local result.
        }
    }

    private void apply(Session session, String edited) {
        try {
            if (stopped || current != session || System.currentTimeMillis() >= session.expires) return;
            CommandSender sender = recipient(session);
            if (sender == null || !allowed.test(sender)) throw new IOException("Command sender must be online and still have betterrtp.editor.");
            Path backup = EditorConfig.apply(configPath(), session.bytes, edited, () -> {
                reload.accept(sender);
                String loaded = loadedConfig.get();
                String persisted = new String(EditorConfig.read(configPath()), StandardCharsets.UTF_8);
                if (!EditorConfig.equivalent(persisted, loaded))
                    throw new IOException("Loaded configuration does not match the saved settings.");
            });
            session.result = "applied";
            sender.sendMessage("§aEditor changes applied. Backup: " + backup.getFileName());
        } catch (Exception error) {
            session.result = "rejected"; message(session, "Not applied: " + error.getMessage());
        } finally { session.applying = false; }
    }

    private JsonObject request(Session session, String method, String path, JsonObject body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(WEBSITE + path).openConnection();
        connection.setConnectTimeout(5000); connection.setReadTimeout(10000); connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method); connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("X-Editor-Actor", "plugin");
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
        volatile String pluginToken, result;
        volatile boolean applying;
        volatile long expires = System.currentTimeMillis() + 15 * 60 * 1000;
        Session(String original, byte[] bytes, UUID player) { this.original = original; this.bytes = bytes; this.player = player; }
    }
}
