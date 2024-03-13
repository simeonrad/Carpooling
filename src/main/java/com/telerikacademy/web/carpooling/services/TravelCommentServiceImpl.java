package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.TravelComment;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelCommentRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.services.contracts.TravelCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelCommentServiceImpl implements TravelCommentService {

    private final TravelCommentRepository travelCommentRepository;
    private final TravelRepository travelRepository;

    @Autowired
    public TravelCommentServiceImpl(TravelCommentRepository travelCommentRepository, TravelRepository travelRepository) {
        this.travelCommentRepository = travelCommentRepository;
        this.travelRepository = travelRepository;
    }

    @Override
    public void addOrUpdateComment(int travelId, String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            Travel travel = travelRepository.getById(travelId);
            TravelComment travelComment = travelCommentRepository.findByTravelId(travelId);
            if (travelComment == null) {
                travelComment = new TravelComment();
                travelComment.setTravel(travel);
            }
            travelComment.setComment(comment);
            travelCommentRepository.saveOrUpdateComment(travelComment);
        }
}

    @Override
    public TravelComment findByTravelId(int travelId) {
        return travelCommentRepository.findByTravelId(travelId);
    }

    @Override
    public void deleteCommentByTravelId(int travelId) {
        travelCommentRepository.deleteCommentByTravelId(travelId);
    }

}
