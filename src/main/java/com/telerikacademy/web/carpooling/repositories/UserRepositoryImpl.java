package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Override
    public void create(TravelApplication application) {

    }

    @Override
    public void update(TravelApplication application) {

    }

    @Override
    public void delete(TravelApplication application) {

    }

    @Override
    public User getById(int id) {
        return null;
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
