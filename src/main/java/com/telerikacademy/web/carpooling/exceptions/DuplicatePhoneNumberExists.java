package com.telerikacademy.web.carpooling.exceptions;

public class DuplicatePhoneNumberExists extends RuntimeException{
    public DuplicatePhoneNumberExists(String type, String attribute, String value) {
        super(String.format
                ("%s with %s %s already exists!", type, attribute, value));
    }
}
