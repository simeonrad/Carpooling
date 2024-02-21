package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import com.telerikacademy.web.carpooling.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticationHelper {
    public static final String USERNAME_HEADER_NAME = "username";
    public static final String PASSWORD_HEADER_NAME = "password";
    public static final String AUTHENTICATION_FAILURE_MESSAGE = "Wrong username or password";
    private final UserService service;
    private final UserRepository repository;

    public AuthenticationHelper(UserService service, UserRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(USERNAME_HEADER_NAME) || !headers.containsKey(PASSWORD_HEADER_NAME)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You need to log in in order to retrieve the required resource!");
        }


        try {
            String username = headers.getFirst(USERNAME_HEADER_NAME);
            String password = headers.getFirst(PASSWORD_HEADER_NAME);
            User user = repository.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid authentication! Password is incorrect!");
            }
            return user;
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid username!");
        }
    }

    public User tryGetUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new AuthenticationFailureException("No user logged in");
        }
        return currentUser;
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = repository.getByUsername(username);
            if (!user.getPassword().equals(password)){
                throw new AuthenticationFailureException(AUTHENTICATION_FAILURE_MESSAGE);
            }
            if (user.isDeleted()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException(AUTHENTICATION_FAILURE_MESSAGE);
        }
    }
}
