package com.telerikacademy.web.carpooling.repositories.contracts;

import com.telerikacademy.web.carpooling.models.TravelComment;

public interface TravelCommentRepository {
    void saveOrUpdateComment(TravelComment travelComment);

    TravelComment findByTravelId(int travelId);

    void deleteCommentByTravelId(int travelId);
}
