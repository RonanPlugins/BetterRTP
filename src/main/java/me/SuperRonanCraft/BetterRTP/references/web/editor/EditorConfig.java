package me.SuperRonanCraft.BetterRTP.references.web.editor;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/** Validation is deliberately stricter than the local file loader. No new schema keys in protocol 1. */
public final class EditorConfig {
    public static final int MAX_BYTES = 128 * 1024;
    private static final Pattern TOKENS = Pattern.compile("(^|[\\s\\[\\]{},])[!&*]", Pattern.MULTILINE);
    private EditorConfig() { }

    public static YamlConfiguration parse(String text) throws Exception {
        // Bukkit supports object deserialization through the reserved == key. Reject equals
        // and encoded character escapes before invoking its parser, not only after parsing.
        if (text.indexOf('=') >= 0 || Pattern.compile("\\\\[xuU]").matcher(text).find())
            throw new IOException("Object serialization markers and encoded character escapes are not supported.");
        if (text.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES || TOKENS.matcher(text).find())
            throw new IOException("Configuration too large, or YAML tags/anchors/aliases present.");
        int lines = 0, flow = 0;
        for (String line : text.split("\\n")) {
            if (++lines > 4000 || line.matches("^ {65}.*")) throw new IOException("Configuration is too complex.");
            for (char c : line.toCharArray()) if (c == '[' || c == '{') flow++;
        }
        if (flow > 128) throw new IOException("Too many flow collections.");
        YamlConfiguration parsed = new YamlConfiguration();
        parsed.loadFromString(text);
        if (!parsed.isConfigurationSection("Settings") || !parsed.isConfigurationSection("Default"))
            throw new IOException("Missing Settings or Default section.");
        return parsed;
    }

    public static void validate(String original, String edited) throws Exception {
        YamlConfiguration before = parse(original), after = parse(edited);
        if (!Objects.equals(before.get("Language-File"), after.get("Language-File")))
            throw new IOException("Change Language-File locally, not through the web editor.");
        compare(before, after, "config", 0);
    }

    private static Object normalize(Object value) {
        return value instanceof ConfigurationSection ? ((ConfigurationSection) value).getValues(false) : value;
    }

    private static void compare(Object oldValue, Object newValue, String path, int depth) throws IOException {
        if (depth > 32) throw new IOException("Configuration nesting is too deep.");
        oldValue = normalize(oldValue); newValue = normalize(newValue);
        if (oldValue instanceof Map) {
            if (!(newValue instanceof Map)) throw new IOException("Expected section: " + path);
            Map<?, ?> oldMap = (Map<?, ?>) oldValue, newMap = (Map<?, ?>) newValue;
            if (!oldMap.keySet().equals(newMap.keySet())) throw new IOException("Keep existing setting names: " + path);
            for (Object key : oldMap.keySet()) compare(oldMap.get(key), newMap.get(key), path + "." + key, depth + 1);
            checkRange(newMap, "MinRadius", "MaxRadius", path, true);
            checkRange(newMap, "MinY", "MaxY", path, false);
        } else if (oldValue instanceof List) {
            if (!(newValue instanceof List)) throw new IOException("Expected list: " + path);
            List<?> oldList = (List<?>) oldValue, newList = (List<?>) newValue;
            if (newList.size() > 1000) throw new IOException("List too long: " + path);
            boolean structured = oldList.stream().anyMatch(v -> normalize(v) instanceof Map);
            if (structured && oldList.size() != newList.size()) throw new IOException("Keep existing world/group entries: " + path);
            for (int i = 0; i < newList.size(); i++) {
                Object baseline = structured ? oldList.get(i) : oldList.isEmpty() ? "" : oldList.get(0);
                compare(baseline, newList.get(i), path + "[" + i + "]", depth + 1);
            }
        } else if (oldValue instanceof Number) {
            if (!(newValue instanceof Number)) throw new IOException("Expected number: " + path);
            double n = ((Number) newValue).doubleValue();
            if (!Double.isFinite(n) || n < Integer.MIN_VALUE || n > Integer.MAX_VALUE ||
                    ((oldValue instanceof Integer || oldValue instanceof Long) && n != Math.rint(n)))
                throw new IOException("Invalid number: " + path);
            if (path.endsWith("Radius") && (n < 0 || n > 30000000)) throw new IOException("Invalid radius: " + path);
            if ((path.endsWith(".Time") || path.endsWith(".LockAfter")) && n < 0) throw new IOException("Negative time: " + path);
            if (path.endsWith(".PreloadRadius") && (n < 0 || n > 16)) throw new IOException("PreloadRadius must be 0–16.");
            if (path.endsWith(".MaxAttempts") && (n < 1 || n > 10000)) throw new IOException("MaxAttempts must be 1–10000.");
        } else if (oldValue instanceof Boolean) {
            if (!(newValue instanceof Boolean)) throw new IOException("Expected boolean: " + path);
        } else if (oldValue instanceof String) {
            if (!(newValue instanceof String)) throw new IOException("Expected text: " + path);
            if (path.endsWith(".Shape") && !Arrays.asList("square", "circle").contains(((String) newValue).toLowerCase(Locale.ROOT)))
                throw new IOException("Shape must be square or circle.");
        } else if (!Objects.equals(oldValue, newValue)) throw new IOException("Unsupported setting: " + path);
    }

    private static void checkRange(Map<?, ?> map, String min, String max, String path, boolean strict) throws IOException {
        if (map.get(min) instanceof Number && map.get(max) instanceof Number) {
            double a = ((Number) map.get(min)).doubleValue(), b = ((Number) map.get(max)).doubleValue();
            if (strict ? a >= b : a > b) throw new IOException("Invalid " + min + "/" + max + " range: " + path);
        }
    }

    @FunctionalInterface
    public interface Reload { void run() throws Exception; }

    public static Path apply(Path file, byte[] snapshot, String edited, Reload reload) throws Exception {
        validate(new String(snapshot, StandardCharsets.UTF_8), edited);
        if (!Arrays.equals(read(file), snapshot)) throw new IOException("config.yml changed; start a new session.");
        Path backups = file.getParent().resolve("editor-backups");
        Files.createDirectories(backups);
        Path backup = backups.resolve("config-" + System.currentTimeMillis() + "-" + UUID.randomUUID() + ".yml");
        Files.write(backup, snapshot, StandardOpenOption.CREATE_NEW);
        if (!Arrays.equals(read(file), snapshot)) throw new IOException("Configuration changed during save.");
        replace(file, edited.getBytes(StandardCharsets.UTF_8));
        try {
            reload.run();
            if (!equivalent(edited, new String(read(file), StandardCharsets.UTF_8)))
                throw new IOException("Saved configuration does not match the submission.");
            return backup;
        } catch (Exception failure) {
            replace(file, snapshot);
            try { reload.run(); }
            catch (Exception rollback) {
                IOException error = new IOException("Original file restored but reload failed; restart the server.", failure);
                error.addSuppressed(rollback); throw error;
            }
            throw new IOException("Reload failed; original configuration restored.", failure);
        }
    }

    public static boolean equivalent(String expected, String actual) throws Exception {
        return tree(parse(expected)).equals(tree(parse(actual)));
    }

    private static Object tree(Object value) {
        value = normalize(value);
        if (value instanceof Map) {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) result.put(entry.getKey(), tree(entry.getValue()));
            return result;
        }
        if (value instanceof List) {
            List<Object> result = new ArrayList<>();
            for (Object item : (List<?>) value) result.add(tree(item));
            return result;
        }
        return value;
    }

    public static byte[] read(Path file) throws IOException {
        if (Files.size(file) > MAX_BYTES) throw new IOException("config.yml exceeds 128 KiB.");
        byte[] result = Files.readAllBytes(file);
        if (result.length > MAX_BYTES) throw new IOException("config.yml exceeds 128 KiB.");
        return result;
    }

    public static void replace(Path file, byte[] bytes) throws IOException {
        Path temp = Files.createTempFile(file.getParent(), ".editor-", ".tmp");
        try {
            Files.write(temp, bytes);
            // Fail closed if this filesystem cannot atomically replace the configuration.
            Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally { Files.deleteIfExists(temp); }
    }
}
