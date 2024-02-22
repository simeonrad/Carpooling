package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FilterFeedbackOptions;

import java.util.List;

public interface FeedbackRepository {
    void create (Feedback feedback);
    void update (Feedback feedback);
    void delete (Feedback feedback);
    List<Feedback>getFeedbacksForUser(FilterFeedbackOptions filterFeedbackOptions);
}