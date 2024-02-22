package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.*;

import java.util.List;

public interface FeedbackService {
    void create(Feedback feedback);

    void update(Feedback feedback, User user);

    void delete(Feedback feedback, User user);

    List<Feedback>getForUser(FilterFeedbackOptions filterFeedbackOptions, User user);
}
