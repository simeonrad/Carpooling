package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FilterFeedbackOptions;
import com.telerikacademy.web.carpooling.models.FilterFeedbackOptionsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedbackRepository {
    void create (Feedback feedback);
    void update (Feedback feedback);
    void delete (Feedback feedback);
    List<Feedback>getFeedbacksForUser(FilterFeedbackOptions filterFeedbackOptions);
    Page<Feedback> getMyReceivedFeedbacks(FilterFeedbackOptions filterFeedbackOptions, Pageable pageable);

    Feedback getByTravelId(int travelId, int authorId, int recipientId);
}