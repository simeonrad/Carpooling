package com.telerikacademy.web.carpooling.exceptions;

public class ForbiddenOperationException extends RuntimeException{
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
