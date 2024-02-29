package com.telerikacademy.web.carpooling.exceptions;

public class ForgottenPasswordEmailSentException extends RuntimeException{
    public ForgottenPasswordEmailSentException() {
        super("Email for retrieving new password was already sent to the user!");
    }
}
