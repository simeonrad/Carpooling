package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.Status;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;

public interface StatusRepository {
    Status getByValue(ApplicationStatus value);
}
