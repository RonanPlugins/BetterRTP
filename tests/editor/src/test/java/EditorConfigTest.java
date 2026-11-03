import me.SuperRonanCraft.BetterRTP.references.web.editor.EditorConfig;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class EditorConfigTest {
    private final String original = "Language-File: en.yml\nSettings:\n  MaxAttempts: 32\n  PreloadRadius: 5\nDefault:\n  MinRadius: 10\n  MaxRadius: 1000\n  Shape: square\n  Biomes: []\n";
    private void rejected(String edited) throws Exception {
        try { EditorConfig.validate(original, edited); fail("Expected rejection"); }
        catch (java.io.IOException expected) { }
    }
    @Test public void acceptsValidChange() throws Exception { EditorConfig.validate(original, original.replace("1000", "2000")); }
    @Test public void rejectsWrongTypeAndRange() throws Exception {
        rejected(original.replace("1000", "false"));
        rejected(original.replace("1000", "5"));
        rejected(original.replace("square", "triangle"));
        rejected(original.replace("PreloadRadius: 5", "PreloadRadius: 5000"));
    }
    @Test public void rejectsSchemaAndLanguageChanges() throws Exception {
        rejected(original + "Injected: true\n");
        rejected(original.replace("en.yml", "../secrets.yml"));
        rejected(original.replace("  Shape: square\n", ""));
    }
    @Test public void rejectsAliasesAndTags() throws Exception {
        rejected(original + "Alias: &x value\n");
        rejected(original + "Tag: !!java/object foo\n");
        rejected(original + "Object:\n  ==: org.bukkit.inventory.ItemStack\n");
    }
    @Test public void validatesBundledConfig() throws Exception {
        String bundled = new String(Files.readAllBytes(Paths.get("../../src/main/resources/config.yml")), StandardCharsets.UTF_8);
        EditorConfig.validate(bundled, bundled.replace("MaxRadius: 1000\n", "MaxRadius: 2000\n"));
    }
    @Test public void allowsStaleWritesAndKeepsNewFileAfterReloadFailure() throws Exception {
        Path dir = Files.createTempDirectory("betterrtp-editor-apply"); Path file = dir.resolve("config.yml");
        byte[] snapshot = original.getBytes(StandardCharsets.UTF_8);
        try {
            Files.write(file, snapshot);
            String edited = original.replace("1000", "2000");
            Path backup = EditorConfig.apply(file, snapshot, edited, () -> {});
            assertArrayEquals(snapshot, Files.readAllBytes(backup));
            assertTrue(EditorConfig.equivalent(edited, new String(Files.readAllBytes(file), StandardCharsets.UTF_8)));
            // The original snapshot is now stale; applying it is intentionally allowed.
            EditorConfig.apply(file, snapshot, original, () -> {});
            assertArrayEquals(snapshot, Files.readAllBytes(file));
            byte[] current = Files.readAllBytes(file);
            final int[] reloads = { 0 };
            try {
                EditorConfig.apply(file, current, edited, () -> { reloads[0]++; throw new java.io.IOException("Simulated reload failure"); });
                fail("Reload should fail");
            } catch (java.io.IOException expected) { }
            assertEquals(1, reloads[0]);
            assertArrayEquals(edited.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(file));
            byte[] unchanged = Files.readAllBytes(file);
            try { EditorConfig.apply(file, snapshot, "broken: [", () -> fail("Must not reload")); fail("Must reject invalid config"); }
            catch (Exception expected) { }
            assertArrayEquals(unchanged, Files.readAllBytes(file));
        } finally {
            try (java.util.stream.Stream<Path> files = Files.walk(dir)) {
                files.sorted(java.util.Comparator.reverseOrder()).forEach(p -> { try { Files.delete(p); } catch (java.io.IOException e) { throw new RuntimeException(e); } });
            }
        }
    }
    @Test public void replacesAtomicallyAndBoundsReads() throws Exception {
        Path dir = Files.createTempDirectory("betterrtp-editor-test"); Path file = dir.resolve("config.yml");
        try {
            Files.write(file, original.getBytes(StandardCharsets.UTF_8));
            byte[] replacement = original.replace("1000", "2000").getBytes(StandardCharsets.UTF_8);
            EditorConfig.replace(file, replacement); assertArrayEquals(replacement, EditorConfig.read(file));
            Files.write(file, new byte[EditorConfig.MAX_BYTES + 1]);
            try { EditorConfig.read(file); fail("Oversized read"); } catch (java.io.IOException expected) { }
        } finally { Files.deleteIfExists(file); Files.deleteIfExists(dir); }
    }
}
