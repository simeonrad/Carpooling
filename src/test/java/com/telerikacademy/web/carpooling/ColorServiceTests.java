package com.telerikacademy.web.carpooling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Colour;
import com.telerikacademy.web.carpooling.repositories.contracts.ColourRepository;
import com.telerikacademy.web.carpooling.services.ColourServiceImpl;

import java.util.Arrays;
import java.util.List;

public class ColorServiceTests {

    @Mock
    private ColourRepository colourRepository;

    @InjectMocks
    private ColourServiceImpl colourService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate_ExistingColour() {
        String colourValue = "Red";
        Colour existingColour = new Colour();
        existingColour.setName(colourValue);

        when(colourRepository.getByValue(colourValue)).thenReturn(existingColour);

        Colour result = colourService.create(colourValue);

        verify(colourRepository, times(1)).getByValue(colourValue);
        assertEquals(existingColour.getName(), result.getName(), "The returned colour should match the existing one");
    }

    @Test
    public void testCreate_NewColour() {
        String colourValue = "Green";
        Colour newColour = new Colour();
        newColour.setName(colourValue);

        when(colourRepository.getByValue(colourValue))
                .thenThrow(new EntityNotFoundException("Colour not found"))
                .thenReturn(newColour);

        Colour result = colourService.create(colourValue);

        verify(colourRepository, times(2)).getByValue(colourValue);
        verify(colourRepository, times(1)).create(any(Colour.class));
        assertEquals(newColour.getName(), result.getName(), "The created colour should have the correct name");
    }


    @Test
    public void testDelete() {
        Colour colour = new Colour();
        colour.setName("Blue");

        doNothing().when(colourRepository).delete(colour);

        colourService.delete(colour);

        verify(colourRepository, times(1)).delete(colour);
    }

    @Test
    public void testGetAll() {
        List<Colour> expectedColours = Arrays.asList(new Colour(), new Colour());
        when(colourRepository.getAll()).thenReturn(expectedColours);

        List<Colour> result = colourService.getAll();

        assertEquals(expectedColours, result, "The returned list should match the expected list of colours");
        verify(colourRepository, times(1)).getAll();
    }

    @Test
    public void testGetByValue() {
        String colourValue = "Yellow";
        Colour expectedColour = new Colour();
        expectedColour.setName(colourValue);
        when(colourRepository.getByValue(colourValue)).thenReturn(expectedColour);

        Colour result = colourService.getByValue(colourValue);

        assertEquals(expectedColour, result, "The returned colour should match the expected colour");
        verify(colourRepository, times(1)).getByValue(colourValue);
    }
}