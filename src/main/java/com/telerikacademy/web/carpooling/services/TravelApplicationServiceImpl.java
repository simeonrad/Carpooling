package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.FilterApplicationOptions;
import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.StatusRepository;
import com.telerikacademy.web.carpooling.repositories.TravelApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelApplicationServiceImpl implements TravelApplicationService {

    public static final String ONLY_THE_CREATOR_OF_AN_APPLICATION_CAN_DELETE_IT = "Only the creator of an application can delete it!";
    public static final String ONLY_THE_CREATOR_OF_AN_APPLICATION_CAN_CANCEL_IT = "Only the creator of an application can cancel it!";
    public static final String ONLY_THE_DRIVER_CAN = "Only the driver of an application can approve or decline it!";
    private final TravelApplicationRepository applicationRepository;
    private final StatusRepository statusRepository;

    @Autowired
    public TravelApplicationServiceImpl(TravelApplicationRepository applicationRepository, StatusRepository statusRepository) {
        this.applicationRepository = applicationRepository;
        this.statusRepository = statusRepository;
    }


    @Override
    public void create(TravelApplication application) {
        applicationRepository.create(application);
    }

    @Override
    public void update(User user, TravelApplication application) {
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
        if (application.getPassenger().equals(user)){
            application.setStatus(statusRepository.getByValue(ApplicationStatus.CANCELLED.toString()));
            applicationRepository.update(application);
        }
        throw new UnauthorizedOperationException(ONLY_THE_CREATOR_OF_AN_APPLICATION_CAN_CANCEL_IT);
    }
    @Override
    public void approve(User user, TravelApplication application) {
        if (application.getTravel().getDriver().equals(user)){
            application.setStatus(statusRepository.getByValue(ApplicationStatus.APPROVED.toString()));
            applicationRepository.update(application);
        }
        throw new UnauthorizedOperationException(ONLY_THE_DRIVER_CAN);
    }
       @Override
    public void decline(User user, TravelApplication application) {
        if (application.getTravel().getDriver().equals(user)){
            application.setStatus(statusRepository.getByValue(ApplicationStatus.DECLINED.toString()));
            applicationRepository.update(application);
        }
        throw new UnauthorizedOperationException(ONLY_THE_DRIVER_CAN);
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
    public TravelApplication getById(int id) {
        return applicationRepository.getById(id);
    }

    @Override
    public List<TravelApplication> getByTravelId(int id) {
        return applicationRepository.getByTravelId(id);
    }

}
