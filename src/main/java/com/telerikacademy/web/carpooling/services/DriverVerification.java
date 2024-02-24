package com.telerikacademy.web.carpooling.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DriverVerification {

    public static boolean sendDrivingLicenseInquiry(String licenseNumber, String jwtToken, String apiKey) throws IOException {
        // API URL
        URL url = new URL("https://driver-vehicle-licensing.api.gov.uk/full-driver-enquiry/v1/driving-licences/retrieve");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Setting request method to POST
        connection.setRequestMethod("POST");

        // Setting request headers
        connection.setRequestProperty("Authorization", jwtToken); // JWT token for authentication
        connection.setRequestProperty("X-API-Key", apiKey); // API Key
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        // Request body
        String jsonInputString = "{\"drivingLicenceNumber\": \"" + licenseNumber + "\", \"includeCPC\": false, \"includeTacho\": false, \"acceptPartialResponse\": \"false\"}";

        // Enabling input and output stream for the connection
        connection.setDoOutput(true);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
        }

        return isLicenseValid(response.toString());
    }

    public static boolean isLicenseValid(String jsonResponse) {
        // Simple check based on the presence of "Valid" status in the "licence" part of the response
        return jsonResponse.contains("\"status\": \"Valid\"");
    }
}
