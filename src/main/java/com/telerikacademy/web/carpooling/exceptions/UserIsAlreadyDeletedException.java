package com.telerikacademy.web.carpooling.exceptions;

public class UserIsAlreadyDeletedException extends RuntimeException {

    public UserIsAlreadyDeletedException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }
}
