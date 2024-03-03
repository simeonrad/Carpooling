package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.TravelComment;

public interface TravelCommentService {

    void addOrUpdateComment(int travelId, String comment);

    TravelComment findByTravelId(int travelId);

    void deleteCommentByTravelId(int travelId);
}
