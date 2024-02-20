package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;

import java.util.List;

public interface FeedbackService {
    void create(Feedback feedback, User user, Travel travel);

    void update(Feedback feedback, User user, Travel travel);

    void delete(Feedback feedback, User user);

    List<Feedback>getForUser(FilterUserOptions filterUserOptions, User user);


}
