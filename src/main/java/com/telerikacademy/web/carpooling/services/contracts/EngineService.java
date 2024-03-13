package com.telerikacademy.web.carpooling.services.contracts;

import com.telerikacademy.web.carpooling.models.CarEngine;

import java.util.List;

public interface EngineService {
    CarEngine getByValue(String value);

    List<CarEngine> getAll();
}
