package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.*;
import org.springframework.stereotype.Component;

@Component
public class TravelMapper {
    public TravelMapper() {
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
       return travelDto;
    }
}
