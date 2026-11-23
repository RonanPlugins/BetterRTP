package me.SuperRonanCraft.BetterRTP.references.web;

import org.junit.Test;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

public class LogUploaderTest {
    @Test public void encodesLogContentWithoutLosingFormatting() throws Exception {
        String content = "Quotes: \"hello\"\nUnicode: \u00e9 \u2603\nPath: C:\\server\\logs";
        JSONObject body = (JSONObject) new JSONParser().parse(new String(LogUploader.requestBody(content), StandardCharsets.UTF_8));
        assertEquals(content, body.get("content"));
        assertEquals("BetterRTP", body.get("source"));
    }
    @Test public void acceptsSuccessfulViewerLinksOnly() {
        assertEquals("https://mclo.gs/abc123", LogUploader.viewingUrl("{\"success\":true,\"url\":\"https://mclo.gs/abc123\"}"));
        for (String response : new String[] {"null", "[]", "not json", "{\"key\":\"legacy\"}", "{\"success\":false,\"url\":\"https://mclo.gs/abc\"}", "{\"success\":true,\"url\":\"https://evil.example/abc\"}", "{\"success\":true,\"url\":42}"}) {
            assertNull(response, LogUploader.viewingUrl(response));
        }
    }
}
