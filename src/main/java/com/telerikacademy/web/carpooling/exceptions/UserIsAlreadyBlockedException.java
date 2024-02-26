package com.telerikacademy.web.carpooling.exceptions;

public class UserIsAlreadyBlockedException extends RuntimeException {
    public UserIsAlreadyBlockedException(int userId) {
        super(String.format("User with ID %d is already blocked.", userId));
    }}
