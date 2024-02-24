package com.telerikacademy.web.carpooling.exceptions;


public class InvalidCityException extends RuntimeException {
    public InvalidCityException(String city, String anotherCity) {
        super(String.format("The city %s or %s wasn't found", city, anotherCity));
    }
}
