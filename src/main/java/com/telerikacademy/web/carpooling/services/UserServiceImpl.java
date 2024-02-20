package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public class UserServiceImpl implements UserService{
    @Override
    public void create(User user) {

    }

    @Override
    public void delete(User user, User deletedBy) {

    }

    @Override
    public void update(User user, User updatedBy) {

    }

    @Override
    public List<User> get(FilterUserOptions filterUserOptions, User user) {
        return null;
    }

    @Override
    public void blockUser(String username, User admin) {

    }

    @Override
    public void unblockUser(String username, User admin) {

    }

    @Override
    public void makeAdmin(String username) {

    }

    @Override
    public void unmakeAdmin(String username, User admin) {

    }

    @Override
    public void addProfilePhoto(String photoUrl, User user) {

    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public List<User> getAllNotDeleted() {
        return null;
    }
}
