package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Status;

public interface StatusRepository {

    Status getByValue(String value);
}
