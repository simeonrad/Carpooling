package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.CarEngine;

import java.util.List;

public interface EngineRepository {
    CarEngine getByValue(String value);

    List<CarEngine> getAll();
}
