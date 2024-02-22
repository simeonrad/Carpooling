package com.telerikacademy.web.carpooling.exceptions;

public class DuplicateEmailExists extends RuntimeException {
    public DuplicateEmailExists(String type, String attribute, String value) {
        super(String.format
                ("%s with %s %s already exists!", type, attribute, value));
    }

    public DuplicateEmailExists(String message) {
        super(message);
    }
}
