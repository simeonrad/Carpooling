package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.CarEngine;
import com.telerikacademy.web.carpooling.repositories.contracts.EngineRepository;
import com.telerikacademy.web.carpooling.services.contracts.EngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngineServiceImpl implements EngineService {

    private final EngineRepository engineRepository;

    @Autowired
    public EngineServiceImpl(EngineRepository engineRepository) {
        this.engineRepository = engineRepository;
    }

    @Override
    public CarEngine getByValue(String value) {
        return engineRepository.getByValue(value);
    }
    @Override
    public List<CarEngine> getAll() {
        return engineRepository.getAll();
    }
}
