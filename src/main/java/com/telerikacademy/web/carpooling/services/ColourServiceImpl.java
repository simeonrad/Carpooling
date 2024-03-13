package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.Colour;
import com.telerikacademy.web.carpooling.repositories.contracts.ColourRepository;
import com.telerikacademy.web.carpooling.services.contracts.ColourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColourServiceImpl implements ColourService {

    private final ColourRepository colourRepository;

    @Autowired
    public ColourServiceImpl(ColourRepository colourRepository) {
        this.colourRepository = colourRepository;
    }

    @Override
    public Colour create(String value) {
        try{
            return getByValue(value);
        } catch (EntityNotFoundException e){
            Colour colour = new Colour();
            colour.setName(value);
            colourRepository.create(colour);
            return getByValue(value);
        }
    }
    @Override
    public void delete(Colour colour) {
       colourRepository.delete(colour);
    }

    @Override
    public List<Colour> getAll(){
        return colourRepository.getAll();
    }

    @Override
    public Colour getByValue(String value){
        return colourRepository.getByValue(value);
    }
}
