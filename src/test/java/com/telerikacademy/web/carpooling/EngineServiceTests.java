package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.models.CarEngine;
import com.telerikacademy.web.carpooling.repositories.contracts.EngineRepository;
import com.telerikacademy.web.carpooling.services.EngineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EngineServiceTests {

    @Mock
    private EngineRepository engineRepository;

    @InjectMocks
    private EngineServiceImpl engineService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getByValue_ReturnsCorrectEngine() {
        String engineValue = "V8";
        CarEngine expectedEngine = new CarEngine();
        expectedEngine.setName(engineValue);

        when(engineRepository.getByValue(engineValue)).thenReturn(expectedEngine);

        CarEngine resultEngine = engineService.getByValue(engineValue);

        assertEquals(expectedEngine, resultEngine, "The engine returned by the service should match the expected engine");
        verify(engineRepository, times(1)).getByValue(engineValue);
    }

    @Test
    public void getAll_ReturnsAllEngines() {
        List<CarEngine> expectedEngines = Arrays.asList(new CarEngine(), new CarEngine());

        when(engineRepository.getAll()).thenReturn(expectedEngines);

        List<CarEngine> resultEngines = engineService.getAll();

        assertEquals(expectedEngines, resultEngines, "The list of engines returned by the service should match the expected list of engines");
        verify(engineRepository, times(1)).getAll();
    }


}
