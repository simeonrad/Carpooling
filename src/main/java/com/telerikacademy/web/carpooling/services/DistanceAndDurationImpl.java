package com.telerikacademy.web.carpooling.services;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.telerikacademy.web.carpooling.exceptions.InvalidCityException;
import com.telerikacademy.web.carpooling.services.contracts.DistanceAndDuration;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
    public class DistanceAndDurationImpl implements DistanceAndDuration {
        private static final String BING_MAPS_API_KEY = "Aq6tiRQgJzcgn1579PCo-aG7cIkC6TA6mCi4Z0Wn4tQvDqUtBrn5qYGHwx7MdWG_";
        public int[] getRouteDetails(String startPoint, String endPoint) {
        try {
            String encodedStart = URLEncoder.encode(startPoint, StandardCharsets.UTF_8);
            String encodedEnd = URLEncoder.encode(endPoint, StandardCharsets.UTF_8);

            String url = "http://dev.virtualearth.net/REST/V1/Routes/Driving?" +
                    "wp.0=" + encodedStart + "&wp.1=" + encodedEnd +
                    "&key=" + DistanceAndDurationImpl.BING_MAPS_API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject resourceSets = jsonResponse.getJSONArray("resourceSets").getJSONObject(0);
            JSONObject resources = resourceSets.getJSONArray("resources").getJSONObject(0);
            int travelDistance = resources.getInt("travelDistance");
            int travelDuration = resources.getInt("travelDuration");

            int[] result = new int[2];
            result[0] = travelDistance;
            result[1] = travelDuration / 60;
            return result;
        } catch (IOException | InterruptedException e) {
            throw new InvalidCityException(startPoint, endPoint);
        }
    }
}
