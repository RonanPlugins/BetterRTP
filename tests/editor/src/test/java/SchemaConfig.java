import com.ronanplugins.bettersync.editor.EditorSchema;
import com.ronanplugins.bettersync.editor.EditorFiles;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/** Tests the actual plugin-owned resource, not BetterSync's legacy profile. */
final class SchemaConfig {
    static final int MAX_BYTES = EditorFiles.MAX_BYTES;
    private static EditorSchema schema(String text) throws Exception {
        return new EditorSchema(new String(Files.readAllBytes(Paths.get(text.contains("Flashback:") ? "../../BetterRTPAddons/src/main/resources/META-INF/editor-schema.json" : "../../BetterRTP-Core/src/main/resources/META-INF/editor-schema.json")), StandardCharsets.UTF_8));
    }
    static void validate(String before, String after) throws Exception { schema(before).validate(before, after); }
    static boolean equivalent(String before, String after) throws Exception { return schema(before).equivalent(before, after); }
    static Path apply(Path file, byte[] before, String after, EditorFiles.Reload reload) throws Exception {
        return EditorFiles.apply(schema(new String(before, StandardCharsets.UTF_8)), file, before, after, reload);
    }
    static byte[] read(Path file) throws Exception { return EditorFiles.read(file); }
    static void replace(Path file, byte[] bytes) throws Exception { EditorFiles.replace(file, bytes); }
}
