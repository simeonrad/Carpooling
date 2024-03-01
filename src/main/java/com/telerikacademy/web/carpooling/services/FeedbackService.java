package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FeedbackService {
    void create(Feedback feedback, User author);

    void update(Feedback feedback, User user);

    void delete(Feedback feedback, User user);

    Feedback getByTravelId(int travelId, int authorId, int recipientId);

    List<Feedback> getForUser(FilterFeedbackOptions filterFeedbackOptions, User user);

    Page<Feedback> getMyReceivedFeedbacks(User user, int page, int size);


    boolean isAuthorAParticipantInTravel(int authorId, int travelId);

    boolean isRecipientAParticipantInTravel(int recipientId, int travelId);

    boolean userHasAlreadyGivenFeedbackForTheRecipient(int authorId, int recipientId, int travelId);

    boolean driverHasAlreadyGivenFeedbackForTheRecipient(int driverId, int recipientId, int travelId);
}
