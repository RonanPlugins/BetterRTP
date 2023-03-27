package me.SuperRonanCraft.BetterRTP.references.web;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LogUploader {
    private static final String UPLOAD_URL = "https://logs.ronanplugins.com/documents";

   public static void main(String[] requestBody) throws IOException {
        URL url = new URL(UPLOAD_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setDoOutput(true);

        //String requestBody = "This is the raw body of text.";

        try (OutputStream outputStream = connection.getOutputStream()) {
            for (String str : requestBody) {
                byte[] input = str.getBytes(StandardCharsets.UTF_8);
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

        System.out.println(response);
    }

    public void sendConfigAsPostRequest() throws IOException {
        FileConfiguration config = getConfig();

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

        //getLogger().log(Level.INFO, "Response: " + response.toString());
    }
}
