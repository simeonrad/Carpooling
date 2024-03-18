package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.Location;

import java.util.List;

public interface LocationRepository {
    void create(Location location);

    void update(Location location);

    void delete(Location location);

    Location getById(int id);

    Location getByValue(String value);

    List<Location> getAll();
}
