package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.TravelApplicationDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TravelApplicationMapper {
    private final TravelService travelService;

    @Autowired
    public TravelApplicationMapper(TravelService travelService) {
        this.travelService = travelService;
    }
    public TravelApplication fromDto(TravelApplicationDto travelApplicationDto, User author) {
        TravelApplication travelApplication = new TravelApplication();
        travelApplication.setTravel(travelService.getById(travelApplicationDto.getTravelId()));
        travelApplication.setPassenger(author);
        travelApplication.setHasBaggage(travelApplicationDto.isHasBaggage());
        travelApplication.setWantToSmoke(travelApplicationDto.isWantToSmoke());
        return travelApplication;
    }

    public TravelApplicationDto toDto(TravelApplication travelApplication) {
        TravelApplicationDto travelApplicationDto = new TravelApplicationDto();
        travelApplicationDto.setTravelId(travelApplication.getId());
        travelApplicationDto.setPassengerUsername(travelApplication.getPassenger().getUsername());
        travelApplicationDto.setStatus(travelApplication.getStatus().getStatus().toString());
        travelApplicationDto.setHasBaggage(travelApplication.isHasBaggage());
        travelApplicationDto.setWantToSmoke(travelApplication.isWantToSmoke());
        return travelApplicationDto;
    }
}
