package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.Colour;

import java.util.List;

public interface ColourRepository {
    void create(Colour colour);

    void update(Colour colour);

    void delete(Colour colour);

    Colour getById(int id);

    Colour getByValue(String value);

    List<Colour> getAll();
}
