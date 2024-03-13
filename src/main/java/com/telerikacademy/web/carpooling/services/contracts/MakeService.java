package com.telerikacademy.web.carpooling.services.contracts;

import com.telerikacademy.web.carpooling.models.Make;

import java.util.List;

public interface MakeService {
    Make create(String make);

    void delete(Make make);

    List<Make> getAll();

    Make getByValue(String value);
}
