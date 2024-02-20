package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;
public interface TravelApplicationService {
    void create(User user, Travel travel);

    void update(User user, Travel travel);

    void delete(User user, Travel travel);

    void cancel(User user, Travel travel);
}
