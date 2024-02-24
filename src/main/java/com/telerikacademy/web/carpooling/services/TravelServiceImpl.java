package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.StatusRepository;
import com.telerikacademy.web.carpooling.repositories.TravelRepository;
import com.telerikacademy.web.carpooling.repositories.TravelRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TravelServiceImpl implements TravelService {
    private final TravelRepository travelRepository;
    private final StatusRepository statusRepository;
    private final DistanceAndDuration distanceAndDuration;

    @Autowired
    public TravelServiceImpl(TravelRepository travelRepository, StatusRepository statusRepository, DistanceAndDuration distanceAndDuration) {
        this.travelRepository = travelRepository;
        this.statusRepository = statusRepository;
        this.distanceAndDuration = distanceAndDuration;
    }

    @Override
    public void create(Travel travel, User user) {
        if (!user.isBlocked()) {
            travel.setDriver(user);
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.PLANNED.toString()));
            int[] travelDetails = distanceAndDuration.getRouteDetails(travel.getStartPoint(), travel.getEndPoint());
            travel.setDistance_km(travelDetails[0]);
            travel.setDurationMinutes(travelDetails[1]);
            travelRepository.create(travel);
        } else {
            throw new UnauthorizedOperationException("No create permission, user is blocked");
        }

    }

    @Override
    public void update(Travel travel, User user) {
        if(user.equals(travel.getDriver())) {
            travelRepository.update(travel);
        } else {
            throw new UnauthorizedOperationException("No update permission, user isn't driver");
        }

    }

    @Override
    public void delete(Travel travel, User user) {
        if(user.getRole().getName().equals("Admin")) {
            travelRepository.delete(travel);
        } else {
            throw new UnauthorizedOperationException("No delete permission, user isn't admin");
        }
    }

    @Override
    public List<Travel> getAll() {
        return travelRepository.getAll();
    }

    @Override
    public Travel getById(int id) {
        return travelRepository.getById(id);
    }

    @Override
    public List<Travel> get(FilterTravelOptions filterTravelOptions) {
        return travelRepository.get(filterTravelOptions);
    }

    @Override
    public void cancel(User user, Travel travel) {
        if (travel.getDriver().equals(user)) {
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.CANCELLED.toString()));
            travelRepository.update(travel);
        } else {
            throw new UnauthorizedOperationException("No cancel permission, user isn't driver.");
        }
    }

    @Override
    public void complete(User user, Travel travel) {
        if (travel.getDriver().equals(user)) {
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.COMPLETED.toString()));
            travelRepository.update(travel);
        } else {
            throw new UnauthorizedOperationException("No complete permission, user isn't driver.");
        }
    }
}
