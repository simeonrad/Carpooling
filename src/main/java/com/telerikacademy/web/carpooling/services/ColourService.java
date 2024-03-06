package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.Colour;

import java.util.List;

public interface ColourService {
    Colour create(String value);

    void delete(Colour colour);

    List<Colour> getAll();

    Colour getByValue(String value);
}
