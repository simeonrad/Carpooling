package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;
import java.util.List;

public interface UserService {
    void create(User user);
    void delete(User user, User deletedBy);
    void delete(User user);
    void update(User user, User updatedBy);
    void update(User user);
    List<User> get(FilterUserOptions filterUserOptions, User user);
    List<User> get(FilterUserOptions filterUserOptions);
    void blockUser(String username, User admin);
    void unblockUser(String username, User admin);
    void makeAdmin(String username, User admin);
    void unmakeAdmin(String username, User admin);
    void addProfilePhoto(String photoUrl, User user);
    List<User> getAll();
    User get(int id);
    User get(String username);
    User getByUI(String UI);
    List<User> getAllNotDeleted();
    void verifyUser(String username);
    void sendForgottenPasswordEmail(User user);
    String generateUI();
    }