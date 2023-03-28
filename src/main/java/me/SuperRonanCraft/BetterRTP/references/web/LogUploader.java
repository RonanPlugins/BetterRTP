package me.SuperRonanCraft.BetterRTP.references.web;

import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class LogUploader {

    private static final String UPLOAD_URL = "https://logs.ronanplugins.com/documents";
    public static final String KEY_URL = "https://logs.ronanplugins.com/";

   public static String post(List<String> requestBody) {
        try {
            URL url = new URL(UPLOAD_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setDoOutput(true);

            //String requestBody = "This is the raw body of text.";

            try (OutputStream outputStream = connection.getOutputStream()) {
                for (String str : requestBody) {
                    byte[] input = (str + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
                    outputStream.write(input, 0, input.length);
                }
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String sendConfigAsPostRequest(FileOther.FILETYPE file) throws IOException {
        FileConfiguration config = file.getConfig();

        // Convert the config file to a YAML string
        String requestBody = config.saveToString();

        // Create the connection to the server
        URL url = new URL(UPLOAD_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/yaml");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
        //getLogger().log(Level.INFO, "Response: " + response.toString());
    }
}
