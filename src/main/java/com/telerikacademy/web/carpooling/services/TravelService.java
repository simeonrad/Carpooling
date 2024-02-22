package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public interface TravelService {
    void create(Travel travel, User user);

    void update(Travel travel, User user);

    void delete(Travel travel, User user);

    List<Travel> getAll();
    Travel getById(int id);
    List<Travel> get(FilterTravelOptions filterTravelOptions);
    void cancel(User user, Travel travel);
    void complete(User user, Travel travel);
}
