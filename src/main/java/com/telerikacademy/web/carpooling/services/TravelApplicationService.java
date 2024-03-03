package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TravelApplicationService {
    void create(TravelApplication application);

    void checkIfCreated(Travel travel, User user);

    void update(User user, TravelApplication application);
    void update(TravelApplication application);

    void delete(User user, TravelApplication application);

    void cancel(User user, TravelApplication application);

    void approve(User user, TravelApplication application);

    void decline(User user, TravelApplication application);

    List<TravelApplication> getAll();

    List<TravelApplication> get(FilterApplicationOptions filterApplicationOptions);
    Page<TravelApplication> getMyTravelApplications(FilterMyApplicationsOptions filterMyApplicationsOptions, Pageable pageable);


    TravelApplication getById(int id);

    List<TravelApplication> getByTravelId(int id);

}
