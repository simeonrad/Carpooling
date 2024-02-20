package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public class TravelServiceImpl implements TravelService{
    @Override
    public void create(Travel travel) {

    }

    @Override
    public void update(Travel travel) {

    }

    @Override
    public void delete(Travel travel) {

    }

    @Override
    public void applyForTravel(User user, Travel travel) {

    }

    @Override
    public List<Travel> getAll() {
        return null;
    }

    @Override
    public List<User> getApplicants(Travel travel) {
        return null;
    }

    @Override
    public void approveApplicant(User user, Travel travel) {

    }

    @Override
    public void rejectApplicant(User user, Travel travel) {

    }

    @Override
    public List<Travel> get(FilterTravelOptions filterUserOptions, Travel travel) {
        return null;
    }

    @Override
    public void cancel(User user, Travel travel) {

    }

    @Override
    public void complete(User user, Travel travel) {

    }
}
