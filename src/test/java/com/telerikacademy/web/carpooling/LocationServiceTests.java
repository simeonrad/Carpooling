package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Colour;
import com.telerikacademy.web.carpooling.models.Location;
import com.telerikacademy.web.carpooling.repositories.contracts.ColourRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.LocationRepository;
import com.telerikacademy.web.carpooling.services.ColourServiceImpl;
import com.telerikacademy.web.carpooling.services.LocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LocationServiceTests {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate_ExistingColour() {
        String locationValue = "London";
        Location existingLocation = new Location();
        existingLocation.setValue(locationValue);

        when(locationRepository.getByValue(locationValue)).thenReturn(existingLocation);

        Location result = locationService.create(locationValue);

        verify(locationRepository, times(1)).getByValue(locationValue);
        assertEquals(existingLocation.getValue(), result.getValue(), "The returned location should match the existing one");
    }

    @Test
    public void testCreate_NewColour() {
        String colourValue = "Sofia";
        Location newLocation = new Location();
        newLocation.setValue(colourValue);

        when(locationRepository.getByValue(colourValue))
                .thenThrow(new EntityNotFoundException("Location not found"))
                .thenReturn(newLocation);

        Location result = locationService.create(colourValue);

        verify(locationRepository, times(2)).getByValue(colourValue);
        verify(locationRepository, times(1)).create(any(Location.class));
        assertEquals(newLocation.getValue(), result.getValue(), "The created location should have the correct name");
    }


    @Test
    public void testDelete() {
        Location location = new Location();
        location.setValue("Pernik");

        doNothing().when(locationRepository).delete(location);

        locationService.delete(location);

        verify(locationRepository, times(1)).delete(location);
    }

    @Test
    public void testGetAll() {
        List<Location> expectedColours = Arrays.asList(new Location(), new Location());
        when(locationRepository.getAll()).thenReturn(expectedColours);

        List<Location> result = locationService.getAll();

        assertEquals(expectedColours, result, "The returned list should match the expected list of locations");
        verify(locationRepository, times(1)).getAll();
    }

    @Test
    public void testGetByValue() {
        String locationValue = "Pleven";
        Location expectedLocation = new Location();
        expectedLocation.setValue(locationValue);
        when(locationRepository.getByValue(locationValue)).thenReturn(expectedLocation);

        Location result = locationService.getByValue(locationValue);

        assertEquals(expectedLocation, result, "The returned location should match the expected colour");
        verify(locationRepository, times(1)).getByValue(locationValue);
    }
}