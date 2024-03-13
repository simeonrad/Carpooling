package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.dtos.TravelApplicationDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.contracts.TravelApplicationService;
import com.telerikacademy.web.carpooling.services.contracts.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TravelApplicationMapper {
    private final TravelService travelService;
    private final TravelApplicationService travelApplicationService;

    @Autowired
    public TravelApplicationMapper(TravelService travelService, TravelApplicationService travelApplicationService) {
        this.travelService = travelService;
        this.travelApplicationService = travelApplicationService;
    }
    public TravelApplication fromDto(TravelApplicationDto travelApplicationDto, User author) {
        TravelApplication travelApplication = new TravelApplication();
        if (travelApplicationDto.getId() > 0) {
            // If ID is present, fetch the existing TravelApplication to update
            travelApplication = travelApplicationService.getById(travelApplicationDto.getId());
        } else {
            // No ID means we're creating a new TravelApplication
            travelApplication = new TravelApplication();
        }
        travelApplication.setTravel(travelService.getById(travelApplicationDto.getTravelId()));
        travelApplication.setPassenger(author);
        travelApplication.setSmoke(travelApplicationDto.getSmoke());
        travelApplication.setLuggage(travelApplicationDto.getLuggage());
        travelApplication.setPet(travelApplicationDto.getPet());
        return travelApplication;
    }

    public TravelApplicationDto toDto(TravelApplication travelApplication) {
        TravelApplicationDto travelApplicationDto = new TravelApplicationDto();
        travelApplicationDto.setId(travelApplication.getId());
        travelApplicationDto.setTravelId(travelApplication.getId());
        travelApplicationDto.setPassengerUsername(travelApplication.getPassenger().getUsername());
        travelApplicationDto.setStatus(travelApplication.getStatus().getStatus().toString());
        travelApplicationDto.setSmoke(travelApplication.getSmoke());
        travelApplicationDto.setLuggage(travelApplication.getLuggage());
        travelApplicationDto.setPet(travelApplication.getPet());
        return travelApplicationDto;
    }
}
