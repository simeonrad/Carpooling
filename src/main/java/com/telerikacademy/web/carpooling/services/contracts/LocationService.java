package com.telerikacademy.web.carpooling.services.contracts;

import com.telerikacademy.web.carpooling.models.Location;

import java.util.List;

public interface LocationService {
    Location create(String value);

    void delete(Location location);

    List<Location> getAll();

    Location getByValue(String value);
}
