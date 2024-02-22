package com.telerikacademy.web.carpooling.exceptions;

public class DuplicateExistsException extends RuntimeException {
    public DuplicateExistsException(String type, String attribute, String value) {
        super(String.format
                ("%s with %s %s already exists!", type, attribute, value));
    }
    public DuplicateExistsException(String message) {
        super(message);
    }
}
