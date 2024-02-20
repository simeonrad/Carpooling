package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public interface TravelService {
    void create(Travel travel);

    void update(Travel travel);

    void delete(Travel travel);

    void applyForTravel(User user, Travel travel);

    List<Travel> getAll();

    List<User> getApplicants(Travel travel);

    void approveApplicant(User user, Travel travel);

    void rejectApplicant(User user, Travel travel);

    List<Travel> get(FilterTravelOptions filterUserOptions, Travel travel);
    void cancel(User user, Travel travel);
    void complete(User user, Travel travel);
}
