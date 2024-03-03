package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.services.TravelCommentService;
import org.springframework.stereotype.Component;

@Component
public class TravelMapper {
    private TravelCommentService travelCommentService;
    public TravelMapper(TravelCommentService travelCommentService) {
        this.travelCommentService = travelCommentService;
    }

    public Travel fromDto(TravelDto travelDto, User author) {
        Travel travel = new Travel();
        travel.setDriver(author);
        travel.setStartPoint(travelDto.getStartPoint());
        travel.setEndPoint(travelDto.getEndPoint());
        travel.setDepartureTime(travelDto.getDepartureTime());
        travel.setFreeSpots(travelDto.getFreeSpots());
        return travel;
    }
    public Travel fromDto(TravelDto travelDto) {
        Travel travel = new Travel();
        travel.setStartPoint(travelDto.getStartPoint());
        travel.setEndPoint(travelDto.getEndPoint());
        travel.setDepartureTime(travelDto.getDepartureTime());
        travel.setFreeSpots(travelDto.getFreeSpots());
        return travel;
    }

    public TravelDto toDto(Travel travel) {
        TravelDto travelDto = new TravelDto();
        travelDto.setDepartureTime(travel.getDepartureTime());
        travelDto.setDriverUsername(travel.getDriver().getUsername());
        travelDto.setStartPoint(travel.getStartPoint());
        travelDto.setEndPoint(travel.getEndPoint());
        travelDto.setFreeSpots(travel.getFreeSpots());
        travelDto.setStatus(travel.getStatus().getStatus().toString());
        travelDto.setDistanceKm(travel.getDistanceKm());
        travelDto.setDurationMinutes(travel.getDurationMinutes());
        TravelComment comment = travelCommentService.findByTravelId(travel.getId());
        if (comment != null) {
            travelDto.setComment(comment.getComment());
        }
       return travelDto;
    }
}
