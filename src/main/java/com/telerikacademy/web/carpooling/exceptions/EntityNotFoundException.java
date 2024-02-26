package com.telerikacademy.web.carpooling.exceptions;


public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String type, int id) {
        this(type, "id", String.valueOf(id));
    }
    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }

    public EntityNotFoundException(String type, int userId, String blocked) {
        super(String.format("%s with %s is not %s.", type, userId, blocked));
    }
}