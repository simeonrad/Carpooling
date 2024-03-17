package com.telerikacademy.web.carpooling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Make;
import com.telerikacademy.web.carpooling.repositories.contracts.MakeRepository;
import com.telerikacademy.web.carpooling.services.MakeServiceImpl;

import java.util.Arrays;
import java.util.List;

public class MakeServiceTests {

    @Mock
    private MakeRepository makeRepository;

    @InjectMocks
    private MakeServiceImpl makeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenMakeExists_thenShouldReturnExistingMake() {
        String value = "ExistingMake";
        Make expectedMake = new Make();
        expectedMake.setName(value);

        when(makeRepository.getByValue(value)).thenReturn(expectedMake);

        Make result = makeService.create(value);

        assertEquals(expectedMake, result);
        verify(makeRepository, never()).create(any(Make.class));
    }

    @Test
    public void whenMakeDoesNotExist_thenShouldCreateMake() {
        String value = "NewMake";
        Make newMake = new Make();
        newMake.setName(value);

        when(makeRepository.getByValue(value)).thenThrow(new EntityNotFoundException("Not found"))
                .thenReturn(newMake);

        Make result = makeService.create(value);

        assertEquals(newMake.getName(), result.getName());
        verify(makeRepository, times(1)).create(any(Make.class));
    }

    @Test
    public void deleteExistingMake() {
        Make make = new Make();

        doNothing().when(makeRepository).delete(make);

        makeService.delete(make);

        verify(makeRepository, times(1)).delete(make);
    }

    @Test
    public void getAllMakes() {
        List<Make> expectedMakes = Arrays.asList(new Make(), new Make());

        when(makeRepository.getAll()).thenReturn(expectedMakes);

        List<Make> result = makeService.getAll();

        assertEquals(expectedMakes, result);
        verify(makeRepository, times(1)).getAll();
    }

    @Test
    public void getByValueExistingMake() {
        String value = "MakeName";
        Make expectedMake = new Make();
        expectedMake.setName(value);

        when(makeRepository.getByValue(value)).thenReturn(expectedMake);

        Make result = makeService.getByValue(value);

        assertEquals(expectedMake, result);
        verify(makeRepository, times(1)).getByValue(value);
    }
}
