package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.StatusRepository;
import com.telerikacademy.web.carpooling.repositories.TravelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TravelServiceImpl implements TravelService {
    private final TravelRepository travelRepository;
    private final StatusRepository statusRepository;

    @Autowired
    public TravelServiceImpl(TravelRepository travelRepository, StatusRepository statusRepository) {
        this.travelRepository = travelRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public void create(Travel travel, User user) {
        if (!user.isBlocked()) {
            ApplicationStatus statusValue = ApplicationStatus.valueOf("PLANNED");
            travel.setStatus(statusRepository.getByValue(statusValue));
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
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.CANCELLED));
            travelRepository.update(travel);
        } else {
            throw new UnauthorizedOperationException("No cancel permission, user isn't driver.");
        }
    }

    @Override
    public void complete(User user, Travel travel) {
        if (travel.getDriver().equals(user)) {
            if (travel.getStatus().getStatus().toString().equals("CANCELLED")) {
                throw new ForbiddenOperationException("Travel that was already cancelled cannot be marked as complete!");
            }
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.COMPLETED));
            travelRepository.update(travel);
        } else {
            throw new UnauthorizedOperationException("No complete permission, user isn't driver.");
        }
    }
}
