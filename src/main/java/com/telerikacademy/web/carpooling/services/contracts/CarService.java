package com.telerikacademy.web.carpooling.services.contracts;

import com.telerikacademy.web.carpooling.models.Car;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public interface CarService {
    void create(Car car, User user);

    void delete(Car car, User user);

    Car getById(int id);

    List<Car> getByUserId(int id);
}
