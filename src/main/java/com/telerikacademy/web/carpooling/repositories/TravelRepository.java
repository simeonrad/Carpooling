package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public interface TravelRepository {

    void create(Travel travel);

    void update(Travel travel);

    void delete(Travel travel);

    List<Travel> get(FilterTravelOptions filterOptions);

    List<Travel> getAll();

    Travel getById(int id);

    List<Travel> findByStartAndEndPoint(String startPoint, String endPoint);
}
