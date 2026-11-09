package me.SuperRonanCraft.BetterRTP.references.web.editor;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/** Fixed settings retain their schema; supported collections allow typed named entries. */
public final class EditorConfig {
    public static final int MAX_BYTES = 128 * 1024;
    private static final Pattern TOKENS = Pattern.compile("(^|[\\s\\[\\]{},])[!&*]", Pattern.MULTILINE);
    private EditorConfig() { }

    private static String withoutQuotedScalars(String text) {
        // Only complete, single-line quoted scalars: Minecraft color codes are text, not YAML anchors.
        return text.replaceAll("\"(?:\\\\[^\\r\\n]|[^\"\\\\\\r\\n])*\"|'(?:''|[^'\\r\\n])*'", "quoted");
    }

    public static YamlConfiguration parse(String text) throws Exception {
        // Bukkit supports object deserialization through the reserved == key. Reject equals
        // and encoded character escapes before invoking its parser, not only after parsing.
        if (text.indexOf("==") >= 0 || Pattern.compile("\\\\[xuU]").matcher(text).find())
            throw new IOException("Object serialization markers and encoded character escapes are not supported.");
        if (text.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES || TOKENS.matcher(withoutQuotedScalars(text)).find())
            throw new IOException("Configuration too large, or YAML tags/anchors/aliases present.");
        int lines = 0, flow = 0;
        for (String line : text.split("\\n")) {
            if (++lines > 4000 || line.matches("^ {65}.*")) throw new IOException("Configuration is too complex.");
            for (char c : line.toCharArray()) if (c == '[' || c == '{') flow++;
        }
        if (flow > 128) throw new IOException("Too many flow collections.");
        YamlConfiguration parsed = new YamlConfiguration();
        parsed.loadFromString(text);
        if (parsed.isConfigurationSection("Flashback")) {
            for (String section : Arrays.asList("Flashback", "Logger", "Portals", "ExtraEffects", "MagicStick", "Commands", "Parties", "RTPMenu"))
                if (!parsed.isConfigurationSection(section)) throw new IOException("Missing addon section: " + section);
        } else if (!parsed.isConfigurationSection("Settings") || !parsed.isConfigurationSection("Default"))
            throw new IOException("Missing Settings or Default section.");
        return parsed;
    }

    public static void validate(String original, String edited) throws Exception {
        YamlConfiguration before = parse(original), after = parse(edited);
        if (!Objects.equals(before.get("Language-File"), after.get("Language-File")))
            throw new IOException("Change Language-File locally, not through the web editor.");
        compare(before, after, "config", 0, (Map<?, ?>) normalize(after.get(after.isConfigurationSection("Flashback") ? "RTPMenu" : "Default")));
        if (after.isConfigurationSection("Flashback")) {
            new java.text.SimpleDateFormat(after.getString("Logger.Format"));
            org.bukkit.Material.valueOf(after.getString("MagicStick.Item.Material").toUpperCase(Locale.ROOT));
        }
    }

    private static Object normalize(Object value) {
        return value instanceof ConfigurationSection ? ((ConfigurationSection) value).getValues(false) : value;
    }

    private static void compare(Object oldValue, Object newValue, String path, int depth, Map<?, ?> defaults) throws IOException {
        if (depth > 32) throw new IOException("Configuration nesting is too deep.");
        oldValue = normalize(oldValue); newValue = normalize(newValue);
        if (path.equals("config.RTPMenu.WorldList") || path.equals("config.Flashback.Timer.Warnings")) {
            addonEntries(oldValue, newValue, path, depth, defaults); return;
        }
        if (Arrays.asList("config.BlacklistedBlocks", "config.DisabledWorlds", "config.Default.Biomes").contains(path)) {
            stringList(newValue, path); return;
        }
        if (Arrays.asList("config.Overrides", "config.WorldType", "config.CustomWorlds", "config.PermissionGroup.Groups").contains(path)) {
            String kind = path.equals("config.Overrides") ? "overrides" : path.equals("config.WorldType") ? "types" : path.equals("config.CustomWorlds") ? "worlds" : "groups";
            named(oldValue, newValue, path, kind, depth, defaults); return;
        }
        if (oldValue instanceof Map) {
            if (!(newValue instanceof Map)) throw new IOException("Expected section: " + path);
            Map<?, ?> oldMap = (Map<?, ?>) oldValue, newMap = (Map<?, ?>) newValue;
            if (!oldMap.keySet().equals(newMap.keySet())) throw new IOException("Keep existing setting names: " + path);
            for (Object key : oldMap.keySet()) compare(oldMap.get(key), newMap.get(key), path + "." + key, depth + 1, defaults);
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
                compare(baseline, newList.get(i), path + "[" + i + "]", depth + 1, defaults);
            }
        } else if (oldValue instanceof Number) {
            if (!(newValue instanceof Number)) throw new IOException("Expected number: " + path);
            double n = ((Number) newValue).doubleValue();
            if (!Double.isFinite(n) || n < Integer.MIN_VALUE || n > Integer.MAX_VALUE ||
                    ((oldValue instanceof Integer || oldValue instanceof Long) && n != Math.rint(n)))
                throw new IOException("Invalid number: " + path);
            if (path.equals("config.RTPMenu.Lines") && (n < 1 || n > 6)) throw new IOException("Menu lines must be 1–6.");
            if (Arrays.asList("config.RTPMenu.AutoRefresh", "config.Flashback.Timer.Delay").contains(path) && n < 0) throw new IOException("Negative time: " + path);
            if (path.equals("config.MagicStick.GiveFirstJoin.Amount") && (n < 1 || n > 64)) throw new IOException("Stick amount must be 1–64.");
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

    private static void addonEntries(Object before, Object after, String path, int depth, Map<?, ?> defaults) throws IOException {
        boolean warnings = path.endsWith("Warnings");
        Map<String, Object> old = addonMap(before, path), next = addonMap(after, path);
        for (Map.Entry<String, Object> entry : next.entrySet()) {
            String key = entry.getKey(); Object value = normalize(entry.getValue());
            if (warnings) {
                try {
                    if (!key.matches("[0-9]+") || Long.parseLong(key) > Integer.MAX_VALUE || !(value instanceof String))
                        throw new IOException("Invalid warning: " + key);
                } catch (NumberFormatException e) { throw new IOException("Invalid warning time."); }
            } else {
                if (!(value instanceof Map)) throw new IOException("Expected menu world settings.");
                Map<?, ?> settings = (Map<?, ?>) value;
                Object previousValue = normalize(old.get(key));
                Map<?, ?> previous = previousValue instanceof Map ? (Map<?, ?>) previousValue : Collections.emptyMap();
                Map<String, Object> schema = new LinkedHashMap<>();
                schema.put("Name", ""); schema.put("Item", "MAP"); schema.put("Slot", 0); schema.put("Lore", Collections.emptyList());
                for (Map.Entry<?, ?> field : settings.entrySet()) {
                    String name = String.valueOf(field.getKey());
                    if (!schema.containsKey(name) && !previous.containsKey(name)) throw new IOException("Unknown menu setting: " + name);
                    compare(schema.containsKey(name) ? schema.get(name) : previous.get(name), field.getValue(), path + "." + key + "." + name, depth + 1, defaults);
                }
                for (Object name : previous.keySet())
                    if (!schema.containsKey(name) && !settings.containsKey(name)) throw new IOException("Keep existing menu setting: " + name);
                Object slot = settings.containsKey("Slot") ? settings.get("Slot") : 0;
                if (!(slot instanceof Number) || ((Number) slot).intValue() < 0 || ((Number) slot).intValue() >= ((Number) defaults.get("Lines")).intValue() * 9)
                    throw new IOException("Menu slot must fit within the configured lines.");
                if (settings.containsKey("Item")) {
                    try { org.bukkit.Material.valueOf(((String) settings.get("Item")).toUpperCase(Locale.ROOT)); }
                    catch (IllegalArgumentException e) { throw new IOException("Unknown menu material."); }
                }
            }
        }
    }

    private static Map<String, Object> addonMap(Object value, String path) throws IOException {
        if (!(value instanceof List) || ((List<?>) value).size() > 1000) throw new IOException("Expected named list: " + path);
        Map<String, Object> result = new LinkedHashMap<>();
        for (Object item : (List<?>) value) {
            item = normalize(item);
            if (!(item instanceof Map) || ((Map<?, ?>) item).isEmpty()) throw new IOException("Expected named entry: " + path);
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) item).entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (!validName(key) || result.containsKey(key)) throw new IOException("Invalid or duplicate name: " + path);
                result.put(key, entry.getValue());
            }
        }
        return result;
    }

    private static boolean validName(String value) {
        return !value.trim().isEmpty() && value.equals(value.trim()) && value.length() <= 100 && !Pattern.compile("[\\x00-\\x1f]").matcher(value).find();
    }

    private static void stringList(Object value, String path) throws IOException {
        if (!(value instanceof List) || ((List<?>) value).size() > 1000) throw new IOException("Expected text list: " + path);
        for (Object item : (List<?>) value)
            if (!(item instanceof String) || ((String) item).trim().isEmpty()) throw new IOException("Expected non-empty text: " + path);
    }

    private static Map<String, Object> entries(Object value, String path) throws IOException {
        if (!(value instanceof List) || ((List<?>) value).size() > 1000) throw new IOException("Expected named list: " + path);
        Map<String, Object> result = new LinkedHashMap<>();
        for (Object item : (List<?>) value) {
            item = normalize(item);
            if (!(item instanceof Map) || ((Map<?, ?>) item).isEmpty()) throw new IOException("Expected named entry: " + path);
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) item).entrySet()) {
                if (!(entry.getKey() instanceof String) || !validName((String) entry.getKey()) || result.containsKey(entry.getKey()))
                    throw new IOException("Invalid or duplicate name: " + path);
                result.put((String) entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private static Map<String, Object> worldSettings(boolean group) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String name : Arrays.asList("MaxRadius", "MinRadius", "CenterX", "CenterZ", "Price", "MaxY", "MinY", "Cooldown")) result.put(name, 0);
        result.put("UseWorldBorder", false); result.put("RTPOnDeath", false);
        result.put("Shape", "square"); result.put("Biomes", Collections.emptyList());
        if (group) result.put("Priority", 0);
        return result;
    }

    private static void named(Object before, Object after, String path, String kind, int depth, Map<?, ?> defaults) throws IOException {
        if (depth > 32) throw new IOException("Configuration nesting is too deep.");
        Map<String, Object> old = entries(before, path), next = entries(after, path);
        for (Map.Entry<String, Object> entry : next.entrySet()) {
            String key = entry.getKey(), at = path + "." + key;
            Object value = normalize(entry.getValue());
            if (kind.equals("overrides")) {
                if (!(value instanceof String) || !validName((String) value)) throw new IOException("Invalid target world: " + at);
            } else if (kind.equals("types")) {
                if (!(value instanceof String) || !Arrays.asList("NORMAL", "NETHER").contains(((String) value).toUpperCase(Locale.ROOT)))
                    throw new IOException("World type must be NORMAL or NETHER: " + at);
            } else if (kind.equals("groups")) {
                named(old.containsKey(key) ? old.get(key) : Collections.emptyList(), value, at, "groupWorlds", depth + 1, defaults);
            } else {
                if (!(value instanceof Map)) throw new IOException("Expected world settings: " + at);
                Map<?, ?> settings = (Map<?, ?>) value;
                Object previousValue = normalize(old.get(key));
                Map<?, ?> previous = previousValue instanceof Map ? (Map<?, ?>) previousValue : Collections.emptyMap();
                Map<String, Object> allowed = worldSettings(kind.equals("groupWorlds"));
                for (Map.Entry<?, ?> field : settings.entrySet()) {
                    if (!(field.getKey() instanceof String)) throw new IOException("Invalid setting name: " + at);
                    String name = (String) field.getKey(); Object val = field.getValue();
                    if (name.equals("Biomes")) stringList(val, at + ".Biomes");
                    else if (allowed.containsKey(name)) {
                        compare(allowed.get(name), val, at + "." + name, depth + 1, defaults);
                        if (name.equals("Cooldown") && ((Number) val).doubleValue() < 0) throw new IOException("Negative cooldown: " + at);
                    } else if (previous.containsKey(name)) compare(previous.get(name), val, at + "." + name, depth + 1, defaults);
                    else throw new IOException("Unsupported world setting: " + at + "." + name);
                }
                for (Object name : previous.keySet())
                    if (!allowed.containsKey(name) && !settings.containsKey(name)) throw new IOException("Keep existing setting: " + at + "." + name);
                Map<Object, Object> effective = new LinkedHashMap<>(); effective.putAll(defaults); effective.putAll(settings);
                checkRange(effective, "MinRadius", "MaxRadius", at, true);
                checkRange(effective, "MinY", "MaxY", at, false);
            }
        }
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
        Path backups = file.getParent().resolve("editor-backups");
        Files.createDirectories(backups);
        Path backup = backups.resolve("config-" + System.currentTimeMillis() + "-" + UUID.randomUUID() + ".yml");
        // Back up the current file, but intentionally allow intervening local changes.
        Files.write(backup, read(file), StandardOpenOption.CREATE_NEW);
        replace(file, edited.getBytes(StandardCharsets.UTF_8));
        try {
            reload.run();
            if (!equivalent(edited, new String(read(file), StandardCharsets.UTF_8)))
                throw new IOException("Saved configuration does not match the submission.");
        } catch (Exception failure) {
            throw new ReloadFailure(failure);
        }
        return backup;
    }

    public static final class ReloadFailure extends IOException {
        public ReloadFailure(Exception cause) { super("Configuration written, but reload failed. No rollback was performed.", cause); }
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
