package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.Car;

import java.util.List;

public interface CarRepository {
    void create(Car car);

    void update(Car car);

    void delete(Car car);

    Car getById(int id);

    List<Car> getByUserId(int id);

    List<Car> getAll();
}
