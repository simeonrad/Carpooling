package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Colour;
import com.telerikacademy.web.carpooling.models.Location;
import com.telerikacademy.web.carpooling.repositories.contracts.ColourRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.LocationRepository;
import com.telerikacademy.web.carpooling.services.contracts.ColourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements com.telerikacademy.web.carpooling.services.contracts.LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location create(String value) {
        try{
            return getByValue(value);
        } catch (EntityNotFoundException e){
            Location location = new Location();
            location.setValue(value);
            locationRepository.create(location);
            return getByValue(value);
        }
    }
    @Override
    public void delete(Location location) {
       locationRepository.delete(location);
    }

    @Override
    public List<Location> getAll(){
        return locationRepository.getAll();
    }

    @Override
    public Location getByValue(String value){
        return locationRepository.getByValue(value);
    }
}
