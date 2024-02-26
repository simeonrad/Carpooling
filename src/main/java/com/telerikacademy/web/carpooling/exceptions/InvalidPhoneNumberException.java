package com.telerikacademy.web.carpooling.exceptions;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException(String phoneNumber) {
        super(String.format("The provided phone number %s is not valid.", phoneNumber));
    }
}
