package com.telerikacademy.web.carpooling.services.contracts;

import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    void create(User user);
    void checkIfVerified(User user);
    void delete(User user, User deletedBy);
    void delete(User user);
    void deleteUI(User user);
    void update(User user, User updatedBy);
    void update(User user);
    List<User> get(FilterUserOptions filterUserOptions, User user);
    List<User> getTop10Passengers();
    List<User> getTop10Organisers();
    Page<User> get(FilterUserOptions filterUserOptions, User user, Pageable pageable);
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
    boolean isUIExisting(String UI);
    List<User> getAllNotDeleted();
    void verifyUser(String username);
    }