package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.InvalidCityException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public interface DistanceAndDuration {
    int[] getRouteDetails(String startPoint, String endPoint);
}
