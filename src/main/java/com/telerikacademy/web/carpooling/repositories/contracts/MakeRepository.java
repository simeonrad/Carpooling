package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.Make;

import java.util.List;

public interface MakeRepository {
    void create(Make make);

    void update(Make make);

    void delete(Make make);

    Make getById(int id);

    Make getByValue(String value);

    List<Make> getAll();
}
