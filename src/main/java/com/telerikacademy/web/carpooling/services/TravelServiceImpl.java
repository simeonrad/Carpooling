package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.StatusRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.services.contracts.DistanceAndDuration;
import com.telerikacademy.web.carpooling.services.contracts.TravelApplicationService;
import com.telerikacademy.web.carpooling.services.contracts.TravelService;
import com.telerikacademy.web.carpooling.services.contracts.UserBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TravelServiceImpl implements TravelService {
    private final TravelRepository travelRepository;
    private final StatusRepository statusRepository;
    private final DistanceAndDuration distanceAndDuration;
    private final UserBlockService userBlockService;
    private final TravelApplicationService travelApplicationService;

    @Autowired
    public TravelServiceImpl(TravelRepository travelRepository, StatusRepository statusRepository, DistanceAndDuration distanceAndDuration, UserBlockService userBlockService, TravelApplicationService travelApplicationService) {
        this.travelRepository = travelRepository;
        this.statusRepository = statusRepository;
        this.distanceAndDuration = distanceAndDuration;
        this.userBlockService = userBlockService;
        this.travelApplicationService = travelApplicationService;
    }

    @Override
    public void create(Travel travel, User user) {
        if (!userBlockService.isUserBlocked(user)) {
            ApplicationStatus statusValue = ApplicationStatus.valueOf("PLANNED");
            travel.setStatus(statusRepository.getByValue(statusValue));
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.PLANNED));
            int[] travelDetails = distanceAndDuration.getRouteDetails(travel.getStartPoint(), travel.getEndPoint());
            travel.setDistanceKm(travelDetails[0]);
            travel.setDurationMinutes(travelDetails[1]);
            if (!travel.getDriver().equals(travel.getCar().getOwner())) {
                throw new UnauthorizedOperationException("You cannot add someones else car to your travel!");
            }
            travelRepository.create(travel);
        } else {
            throw new UnauthorizedOperationException("No create permission, user is blocked");
        }

    }

    @Override
    public void update(Travel travel, User user) {
        if (user.equals(travel.getDriver())) {
            travelRepository.update(travel);
        } else {
            throw new UnauthorizedOperationException("No update permission, user isn't driver");
        }

    }

    @Override
    public void update(Travel travel) {
        travelRepository.update(travel);
    }

    @Override
    public void delete(Travel travel, User user) {
        if (user.getRole().getName().equals("Admin")) {
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
    public Page<Travel> get(FilterTravelOptions filterTravelOptions, Pageable pageable) {
        return travelRepository.get(filterTravelOptions, pageable);
    }

    @Override
    public Page<Travel> getMyTravels(FilterTravelOptions filterTravelOptions, Pageable page) {
        return travelRepository.getMyTravels(filterTravelOptions, page);
    }

    @Override
    public Page<Travel> getAllMyTravels(FilterTravelOptions filterTravelOptions, Pageable page) {
        return travelRepository.getAllMyTravels(filterTravelOptions, page);
    }

    @Override
    public Page<Travel> getTravelsIParticipatedIn(FilterTravelOptions filterTravelOptions, Pageable page, int currentUserId) {
        return travelRepository.getTravelsIParticipatedIn(filterTravelOptions, page, currentUserId);
    }

    @Override
    public void cancel(User user, Travel travel) {
        if (travel.getDriver().equals(user)) {
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.CANCELLED));
            try {
                for (TravelApplication application : travelApplicationService.getByTravelId(travel.getId())) {
                    application.setStatus(statusRepository.getByValue(ApplicationStatus.DECLINED));
                    travelApplicationService.update(application);
                }
            } catch (EntityNotFoundException ignored){}
            travelRepository.update(travel);
            if (travel.getDepartureTime().isBefore(LocalDateTime.now())) {
                throw new ForbiddenOperationException("You cannot cancel a travel after the departure time");
            }
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
            if (travel.getDepartureTime().isAfter(LocalDateTime.now())) {
                throw new ForbiddenOperationException("You cannot complete a travel before the departure time");
            }
            travel.setStatus(statusRepository.getByValue(ApplicationStatus.COMPLETED));
            travelRepository.update(travel);
        } else {
            throw new UnauthorizedOperationException("No complete permission, user isn't driver.");
        }
    }
}
