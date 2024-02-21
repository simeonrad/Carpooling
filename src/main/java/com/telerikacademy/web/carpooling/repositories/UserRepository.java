package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;
import java.util.List;

public interface UserRepository {
    void create(User user);

    void delete(User user);

    void update(User user);

    List<User> get(FilterUserOptions filterOptions);
    User getById(int id);

    List<User> getAll();

    List<User> getAllNotDeleted();

    User getByUsername(String username);

    User getByEmail(String email);

    boolean updateEmail(String email);
}
