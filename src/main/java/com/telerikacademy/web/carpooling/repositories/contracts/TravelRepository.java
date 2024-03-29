package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.FilterTravelOptions;
import com.telerikacademy.web.carpooling.models.Travel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TravelRepository {

    void create(Travel travel);

    void update(Travel travel);

    void delete(Travel travel);

    List<Travel> get(FilterTravelOptions filterOptions);

    Page<Travel> get(FilterTravelOptions filterOptions, Pageable pageable);

    Page<Travel> getMyTravels(FilterTravelOptions filterOptions, Pageable pageable);
    Page<Travel> getAllMyTravels(FilterTravelOptions filterOptions, Pageable pageable);
    Page<Travel> getTravelsIParticipatedIn(FilterTravelOptions filterOptions, Pageable pageable, int currentUserId);
    List<Travel> getAll();

    Travel getById(int id);

    List<Travel> findByStartAndEndPoint(String startPoint, String endPoint);

    boolean isUserAParticipantInTravel(int userId, int travelId);

    boolean hasUserAlreadyGiveFeedbackForTheRecipient(int authorId, int recipientId, int travelId);

    boolean driverHasAlreadyGivenFeedbackForTheRecipient(int driverId, int recipientId, int travelId);

    boolean isRecipientAParticipantInTravel(int recipientId, int travelId);
}
