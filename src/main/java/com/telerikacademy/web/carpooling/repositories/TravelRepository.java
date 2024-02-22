package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Travel;

import java.util.List;

public interface TravelRepository {

    void create(Travel travel);

    void update(Travel travel);

    void delete(Travel travel);

    List<Travel> getAll();

    Travel getById(int id);

    List<Travel> findByStartAndEndPoint(String startPoint, String endPoint);
}
