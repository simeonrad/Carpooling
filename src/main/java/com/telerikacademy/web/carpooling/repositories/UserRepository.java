package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public interface UserRepository {
    void create(TravelApplication application);

    void update(TravelApplication application);

    void delete(TravelApplication application);

    User getById(int id);

    User getByUsername(String username);

    List<User> getAll();
}
