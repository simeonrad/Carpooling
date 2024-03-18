package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.services.DistanceAndDurationImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DistanceAndDurationTests {

    @Test
    void getRouteDetailsSuccess() throws Exception {
        String fakeResponse = """
            {
              "resourceSets": [{
                "resources": [{
                  "travelDistance": 120,
                  "travelDuration": 7200
                }]
              }]
            }""";

        try (MockedStatic<HttpClient> mockedHttpClient = Mockito.mockStatic(HttpClient.class)) {
            HttpClient client = mock(HttpClient.class);
            HttpResponse<String> response = mock(HttpResponse.class);

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(client);

            when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);
            when(response.body()).thenReturn(fakeResponse);

            DistanceAndDurationImpl service = new DistanceAndDurationImpl();
            int[] result = service.getRouteDetails("Start", "End");

            assertEquals(120, result[0]);
            assertEquals(120, result[1]);
        }
    }
}
