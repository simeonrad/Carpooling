package com.telerikacademy.web.carpooling.services.contracts;

import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TravelService {
    void create(Travel travel, User user);

    void update(Travel travel, User user);
    void update(Travel travel);

    void delete(Travel travel, User user);
    List<Travel> getAll();
    Page<Travel> getMyTravels(FilterTravelOptions filterTravelOptions, Pageable page);
    Page<Travel> getAllMyTravels(FilterTravelOptions filterTravelOptions, Pageable page);
    Page<Travel> getTravelsIParticipatedIn(FilterTravelOptions filterTravelOptions, Pageable page, int currentUserId);
    Travel getById(int id);
    List<Travel> get(FilterTravelOptions filterTravelOptions);
    Page<Travel> get(FilterTravelOptions filterTravelOptions, Pageable pageable);
    void cancel(User user, Travel travel);
    void complete(User user, Travel travel);
}
