package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.*;

import java.util.List;

public interface UserRepository {
    void create(User user);

    void create(NonVerifiedUser nonVerifiedUser);

    void delete(IsDeleted isDeleted);

    void unmarkAsDeleted(IsDeleted isDeleted);

    void update(User user);

    List<User> get(FilterUserOptions filterOptions);
    User getById(int id);

    IsDeleted getDeletedById(int userId);

    List<User> getAll();

    boolean isDeleted (int userId);

    boolean isBlocked(int userId);

    List<User> getAllNotDeleted();

    User getByUsername(String username);

    User getByEmail(String email);

    User getByUI(String UI);

    boolean telephoneExists(String phoneNumber, int currentUserId);

    boolean updateEmail(String email, int currentUserId);

    boolean passwordEmailAlreadySent(User user);

    NonVerifiedUser getNonVerifiedById(int userId);

    void setNewUI(ForgottenPasswordUI forgottenPasswordUI);

    void verify(NonVerifiedUser nonVerifiedUser);
}
