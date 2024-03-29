package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.FilterApplicationOptions;
import com.telerikacademy.web.carpooling.models.FilterMyApplicationsOptions;
import com.telerikacademy.web.carpooling.models.TravelApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TravelApplicationRepository {
    void create(TravelApplication application);

    void update(TravelApplication application);

    void delete(TravelApplication application);

    TravelApplication getById(int id);

    List<TravelApplication> getByTravelId(int id);

    Page<TravelApplication> getMyTravelApplications(FilterMyApplicationsOptions filterMyApplicationsOptions, Pageable pageable, int userId);

    List<TravelApplication> getAll();

    List<TravelApplication> get(FilterApplicationOptions filterApplicationOptions);

    boolean isUserAppliedForTravel(int travelId, int passengerId);
}
