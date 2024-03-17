package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.DuplicateExistsException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.StatusRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelApplicationRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.services.contracts.TravelApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TravelApplicationServiceImpl implements TravelApplicationService {

    public static final String ONLY_THE_CREATOR_OF_AN_APPLICATION_CAN_DELETE_IT = "Only the creator of an application can delete it!";
    public static final String ONLY_THE_CREATOR_OF_AN_APPLICATION_CAN_CANCEL_IT = "Only the creator of an application can cancel it!";
    public static final String ONLY_THE_DRIVER_CAN = "Only the driver of an application can approve or decline it!";
    public static final String CANCELLED_APPLICATIONS_ERR = "Cancelled applications cannot be approved or declined";
    private final TravelApplicationRepository applicationRepository;
    private final TravelRepository travelRepository;
    private final StatusRepository statusRepository;


    @Autowired
    public TravelApplicationServiceImpl(TravelApplicationRepository applicationRepository, TravelRepository travelRepository, StatusRepository statusRepository) {
        this.applicationRepository = applicationRepository;
        this.travelRepository = travelRepository;
        this.statusRepository = statusRepository;
    }


    @Override
    public void create(TravelApplication application) {
        if (application.getPassenger().equals(application.getTravel().getDriver())) {
            throw new ForbiddenOperationException("Travel organizer cannot apply for his own travel!");
        }
        if (application.getTravel().getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new ForbiddenOperationException("A travel with departure date already passed cannot be created");
        }
        application.setStatus(statusRepository.getByValue(ApplicationStatus.PENDING));
        applicationRepository.create(application);
    }

    @Override
    public void checkIfCreated(Travel travel, User user) {
        if (applicationRepository.isUserAppliedForTravel(travel.getId(), user.getId())) {
            throw new DuplicateExistsException("User already applied for travel");
        }
    }

    @Override
    public void update(User user, TravelApplication application) {
        applicationRepository.update(application);
    }

    @Override
    public void update(TravelApplication application) {
        applicationRepository.update(application);
    }

    @Override
    public void delete(User user, TravelApplication application) {
        if (application.getPassenger().equals(user))
            applicationRepository.delete(application);
        throw new UnauthorizedOperationException(ONLY_THE_CREATOR_OF_AN_APPLICATION_CAN_DELETE_IT);
    }

    @Override
    public void cancel(User user, TravelApplication application) {
        if (!application.getPassenger().equals(user)) {
            throw new UnauthorizedOperationException(ONLY_THE_CREATOR_OF_AN_APPLICATION_CAN_CANCEL_IT);
        }
        if (application.getTravel().getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new ForbiddenOperationException("You cannot cancel an application after the departure time");
        }
        Travel travel = application.getTravel();
        if (application.getStatus().getStatus().equals(ApplicationStatus.APPROVED)){
            travel.setFreeSpots(travel.getFreeSpots() + 1);
            travelRepository.update(travel);}
        application.setStatus(statusRepository.getByValue(ApplicationStatus.CANCELLED));
        applicationRepository.update(application);
    }

    @Override
    public void approve(User user, TravelApplication application) {
        if (!application.getTravel().getDriver().equals(user)) {
            throw new UnauthorizedOperationException(ONLY_THE_DRIVER_CAN);
        }
         if (application.getStatus().getStatus().equals(ApplicationStatus.CANCELLED)) {
            throw new ForbiddenOperationException(CANCELLED_APPLICATIONS_ERR);
        }

        if (application.getTravel().getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new ForbiddenOperationException("You cannot approve an application after the departure time");
        }
        Travel travel = application.getTravel();
        travel.setFreeSpots(travel.getFreeSpots() - 1);
        travelRepository.update(travel);
        application.setStatus(statusRepository.getByValue(ApplicationStatus.APPROVED));
        applicationRepository.update(application);
    }

    @Override
    public void decline(User user, TravelApplication application) {
        if (!application.getTravel().getDriver().equals(user)) {
            throw new UnauthorizedOperationException(ONLY_THE_DRIVER_CAN);
        }
        if (application.getTravel().getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new ForbiddenOperationException("You cannot decline an application after the departure time");
        }
        if (application.getStatus().getStatus().equals(ApplicationStatus.CANCELLED)) {
            throw new ForbiddenOperationException(CANCELLED_APPLICATIONS_ERR);
        }
        Travel travel = application.getTravel();
        if (application.getStatus().getStatus().equals(ApplicationStatus.APPROVED)){
        travel.setFreeSpots(travel.getFreeSpots() + 1);
        travelRepository.update(travel);}
        application.setStatus(statusRepository.getByValue(ApplicationStatus.DECLINED));
        applicationRepository.update(application);
    }

    @Override
    public List<TravelApplication> getAll() {
        return applicationRepository.getAll();
    }

    @Override
    public List<TravelApplication> get(FilterApplicationOptions filterApplicationOptions) {
        return applicationRepository.get(filterApplicationOptions);
    }

    @Override
    public Page<TravelApplication> getMyTravelApplications(FilterMyApplicationsOptions filterMyApplicationsOptions, Pageable pageable, int userId) {
        return applicationRepository.getMyTravelApplications(filterMyApplicationsOptions, pageable, userId);
    }

    @Override
    public TravelApplication getById(int id) {
        return applicationRepository.getById(id);
    }

    @Override
    public List<TravelApplication> getByTravelId(int id) {
        return applicationRepository.getByTravelId(id);
    }

}
