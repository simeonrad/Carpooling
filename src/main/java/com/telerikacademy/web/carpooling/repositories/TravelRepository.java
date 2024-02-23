package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;

import java.util.List;

public interface TravelRepository {

    void create(Travel travel);

    void update(Travel travel);

    void delete(Travel travel);

    List<Travel> get(FilterTravelOptions filterOptions);

    List<Travel> getAll();

    Travel getById(int id);

    List<Travel> findByStartAndEndPoint(String startPoint, String endPoint);

    boolean isUserAParticipantInTravel(int userId, int travelId);
    boolean hasUserAlreadyGiveFeedbackForTheRecipient(int authorId, int recipientId, int travelId);
    boolean driverHasAlreadyGivenFeedbackForTheRecipient(int driverId, int recipientId, int travelId);
    boolean isRecipientAParticipantInTravel(int recipientId, int travelId);
}
