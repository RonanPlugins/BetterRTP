package me.SuperRonanCraft.BetterRTP.references.web;

import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogUploader {
    public static final String KEY_URL = "https://mclo.gs/";
    private static final String UPLOAD_URL = "https://api.mclo.gs/1/log";
    private static final int MAX_LOG_BYTES = 5 * 1024 * 1024;

    /** Returns the validated viewing URL, or null when the upload fails. */
    @Nullable
    public static String post(List<String> lines) {
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        for (String line : lines) {
            byte[] bytes = (line + "\n").getBytes(StandardCharsets.UTF_8);
            if (content.size() + bytes.length > MAX_LOG_BYTES) return null;
            content.write(bytes, 0, bytes.length);
        }
        return upload(new String(content.toByteArray(), StandardCharsets.UTF_8));
    }

    @Nullable
    public static String post(File file) {
        if (file == null || !file.isFile() || file.length() > MAX_LOG_BYTES) return null;
        try (InputStream input = new FileInputStream(file)) {
            return upload(readBounded(input, MAX_LOG_BYTES));
        } catch (IOException e) {
            return null;
        }
    }

    private static String readBounded(InputStream input, int limit) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int count;
        while ((count = input.read(buffer)) != -1) {
            if (result.size() + count > limit) throw new IOException("Upload size limit exceeded");
            result.write(buffer, 0, count);
        }
        return new String(result.toByteArray(), StandardCharsets.UTF_8);
    }

    @Nullable
    private static String upload(String content) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            byte[] body = requestBody(content);
            connection.setFixedLengthStreamingMode(body.length);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) return null;
            try (InputStream input = connection.getInputStream()) {
                return viewingUrl(readBounded(input, 64 * 1024));
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    static byte[] requestBody(String content) {
        Map<String, String> request = new LinkedHashMap<>();
        request.put("content", content);
        request.put("source", "BetterRTP");
        return JSONObject.toJSONString(request).getBytes(StandardCharsets.UTF_8);
    }

    @Nullable
    static String viewingUrl(String response) {
        try {
            Object parsed = new JSONParser().parse(response);
            if (!(parsed instanceof JSONObject)) return null;
            JSONObject json = (JSONObject) parsed;
            if (!Boolean.TRUE.equals(json.get("success")) || !(json.get("url") instanceof String)) return null;
            String url = (String) json.get("url");
            // Only show links to the mclo.gs log viewer; never render arbitrary API response text in chat.
            return url.matches("https://mclo\\.gs/[A-Za-z0-9_-]+") ? url : null;
        } catch (ParseException e) {
            return null;
        }
    }
}
