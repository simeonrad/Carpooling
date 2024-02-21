package com.telerikacademy.web.carpooling.exceptions;


public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String mail) {
        super(String.format("The provided email %s is not valid. " +
                "Please try again with a correctly written email address!", mail));
    }
}
